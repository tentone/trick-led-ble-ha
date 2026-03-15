"""BLE communication layer for the Trick LED integration.

Protocol details extracted from the decompiled Android application
(MyBluetoothGatt.java).  All UUIDs and command byte sequences are
defined in :mod:`const`.
"""

from __future__ import annotations

import asyncio
import logging
from collections.abc import Callable
from typing import TYPE_CHECKING

from bleak import BleakClient
from bleak.exc import BleakError
from bleak_retry_connector import establish_connection

from .const import (
    BLE_CHAR_NOTIFY_UUID,
    BLE_CHAR_WRITE_UUID,
    CMD_BRIGHTNESS_PREFIX,
    CMD_BRIGHTNESS_SUFFIX,
    CMD_GET_STATE,
    CMD_TURN_OFF,
    CMD_TURN_ON,
)

if TYPE_CHECKING:
    from bleak.backends.device import BLEDevice

from .models import TrickLedDeviceState

_LOGGER = logging.getLogger(__name__)


class TrickLedBleClient:
    """Manages the BLE connection to a single Trick LED device.

    Usage::

        client = TrickLedBleClient(ble_device)
        await client.turn_on()
        await client.set_brightness(128)
        await client.turn_off()
    """

    def __init__(self, ble_device: BLEDevice) -> None:
        self._ble_device = ble_device
        self._client: BleakClient | None = None
        # Persistent notification support
        self._notifications_active: bool = False
        self._state_change_callback: Callable[[TrickLedDeviceState], None] | None = None
        # Used by poll_state() to capture a single response when notifications are active
        self._pending_poll_event: asyncio.Event | None = None
        self._pending_poll_state: TrickLedDeviceState | None = None

    # ── Connection helpers ────────────────────────────────────────────────────

    async def _connect(self) -> BleakClient:
        """Return a connected :class:`BleakClient`, establishing one if needed.

        Uses :func:`bleak_retry_connector.establish_connection` which handles reconnection retries transparently.
        
        When persistent notifications are active and a new connection is established, the subscription is automatically restarted on the fresh client.
        """
        if self._client is None or not self._client.is_connected:
            self._client = await establish_connection(
                BleakClient,
                self._ble_device,
                self._ble_device.address,
            )
            # Re-subscribe after reconnection so remote-triggered notifications
            # continue to be received without any action from the coordinator.
            if self._notifications_active:
                try:
                    await self._client.start_notify(
                        BLE_CHAR_NOTIFY_UUID, self._handle_notification
                    )
                    _LOGGER.debug(
                        "Re-subscribed to notifications on %s after reconnect",
                        self._ble_device.address,
                    )
                except BleakError:
                    _LOGGER.warning(
                        "Failed to re-subscribe to notifications on %s",
                        self._ble_device.address,
                    )
        return self._client

    async def disconnect(self) -> None:
        """Disconnect from the device if a connection is open."""
        if self._client and self._client.is_connected:
            await self._client.disconnect()
        self._client = None

    # ── Notification helpers ──────────────────────────────────────────────────

    def _handle_notification(self, _: int, data: bytearray) -> None:
        """Unified handler for all BLE notifications from the device.

        Called by bleak on the asyncio event loop thread, so access to shared  state is safe without additional locking.

        Parses the device state packet and:

        * Signals any in-progress :meth:`poll_state` call via
          ``_pending_poll_event``.
        * Calls the persistent ``_state_change_callback`` (if registered) so that the coordinator can push the new state to Home Assistant  immediately
        
        e.g. when the remote changes the power state.

        The device notification format (extracted from the Android app):
        * Byte 0: ``0x66`` – packet header / magic byte
        * Byte 2: ``0x23`` = ON, ``0x24`` = OFF
        """
        if len(data) < 4 or data[0] != 0x66:
            return

        state = TrickLedDeviceState()
        state.is_on = data[2] == 0x23

        # Capture local references to avoid TOCTOU on the instance attributes.
        pending_state = self._pending_poll_state
        pending_event = self._pending_poll_event
        callback = self._state_change_callback

        # Signal a waiting poll_state() call
        if pending_state is not None:
            pending_state.is_on = state.is_on
        if pending_event is not None:
            pending_event.set()

        # Notify the coordinator of an unsolicited state change (e.g. remote)
        if callback is not None:
            callback(state)

    async def start_notifications(
        self, callback: Callable[[TrickLedDeviceState], None]
    ) -> None:
        """Subscribe persistently to device state notifications.

        Once started, the device will push state updates to *callback* whenever its power state changes
        
        for example when a physical remote is used.
        
        The subscription is automatically re-established after a reconnection.

        Args:
            callback: Invoked with the new :class:`~.models.TrickLedDeviceState`
                      each time the device sends a notification.
        """
        self._state_change_callback = callback
        # Connect first (with _notifications_active still False so _connect() does not attempt an early re-subscribe on this initial call).
        client = await self._connect()
        await client.start_notify(BLE_CHAR_NOTIFY_UUID, self._handle_notification)
        # Mark active only after the subscription succeeds so that any reconnection in _connect() also correctly re-subscribes.
        self._notifications_active = True
        _LOGGER.debug(
            "Started persistent notifications for %s", self._ble_device.address
        )

    async def stop_notifications(self) -> None:
        """Unsubscribe from device state notifications.

        Safe to call even if notifications were never started or if the device is already disconnected.
        """
        self._state_change_callback = None
        self._notifications_active = False
        if self._client and self._client.is_connected:
            try:
                await self._client.stop_notify(BLE_CHAR_NOTIFY_UUID)
            except BleakError:
                _LOGGER.debug(
                    "stop_notify failed for %s (already disconnected?)",
                    self._ble_device.address,
                )
        _LOGGER.debug(
            "Stopped persistent notifications for %s", self._ble_device.address
        )

    # ── Private write helper ──────────────────────────────────────────────────

    async def _write(self, data: bytes) -> None:
        """Write *data* to the command characteristic.

        Args:
            data: Raw bytes to send to the device.

        Raises:
            BleakError: If the write fails after all retries are exhausted.
        """
        try:
            client = await self._connect()
            await client.write_gatt_char(
                BLE_CHAR_WRITE_UUID,
                data,
                response=False,
            )
            _LOGGER.debug("Wrote %s to %s", data.hex(), self._ble_device.address)
        except BleakError:
            _LOGGER.error(
                "Failed to write to %s, disconnecting", self._ble_device.address
            )
            await self.disconnect()
            raise

    # ── Public control API ────────────────────────────────────────────────────

    async def turn_on(self) -> None:
        """Power on the LED strip."""
        await self._write(CMD_TURN_ON)

    async def turn_off(self) -> None:
        """Power off the LED strip."""
        await self._write(CMD_TURN_OFF)

    async def set_brightness(self, brightness: int) -> None:
        """Set the brightness of the LED strip.

        Args:
            brightness: Desired brightness in the range ``0``–``255``.
                        ``0`` is minimum (off/dim), ``255`` is maximum.

        The wire format is ``0x56 <brightness_byte> 0x00 0x00 0x00 0xF0 0xAA``
        """
        brightness = max(0, min(255, brightness))
        data = CMD_BRIGHTNESS_PREFIX + bytes([brightness]) + CMD_BRIGHTNESS_SUFFIX
        await self._write(data)

    async def poll_state(self) -> TrickLedDeviceState:
        """Query the device for its current state.

        Sends the ``0xEF 0x01 0x77`` state-request command and waits for the notification response on :data:`~.const.BLE_CHAR_NOTIFY_UUID`.

        The device replies with an 8- or 12-byte packet whose first byte is
        ``0x66``.  Byte at index 2 indicates power state:
        ``0x23`` = ON,
        ``0x24`` = OFF.
        
        For 12-byte responses bytes 6–8 carry R, G, B values.

        When persistent notifications are already active the existing subscription is reused and no double-subscribe is attempted.

        Returns:
            A :class:`~.models.TrickLedDeviceState` populated with the values
            read from the device.
        """
        state = TrickLedDeviceState()
        event = asyncio.Event()

        # Register the pending poll targets so _handle_notification can fill them.
        self._pending_poll_state = state
        self._pending_poll_event = event

        try:
            client = await self._connect()

            if not self._notifications_active:
                # No persistent subscription yet – use a temporary one.
                await client.start_notify(BLE_CHAR_NOTIFY_UUID, self._handle_notification)

            try:
                await self._write(CMD_GET_STATE)
                async with asyncio.timeout(3.0):
                    await event.wait()
            finally:
                if not self._notifications_active:
                    await client.stop_notify(BLE_CHAR_NOTIFY_UUID)
        except (BleakError, TimeoutError):
            _LOGGER.debug(
                "poll_state failed for %s, returning cached state",
                self._ble_device.address,
            )
        finally:
            self._pending_poll_state = None
            self._pending_poll_event = None

        return state
