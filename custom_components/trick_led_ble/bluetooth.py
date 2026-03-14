"""BLE communication layer for the Trick LED integration.

Protocol details extracted from the decompiled Android application
(MyBluetoothGatt.java).  All UUIDs and command byte sequences are
defined in :mod:`const`.
"""

from __future__ import annotations

import asyncio
import logging
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

    # ── Connection helpers ────────────────────────────────────────────────────

    async def _connect(self) -> BleakClient:
        """Return a connected :class:`BleakClient`, establishing one if needed.

        Uses :func:`bleak_retry_connector.establish_connection` which handles
        reconnection retries transparently.
        """
        if self._client is None or not self._client.is_connected:
            self._client = await establish_connection(
                BleakClient,
                self._ble_device,
                self._ble_device.address,
            )
        return self._client

    async def disconnect(self) -> None:
        """Disconnect from the device if a connection is open."""
        if self._client and self._client.is_connected:
            await self._client.disconnect()
        self._client = None

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
        as extracted from ``MyBluetoothGatt.setColor()`` in the Android app.
        """
        brightness = max(0, min(255, brightness))
        data = CMD_BRIGHTNESS_PREFIX + bytes([brightness]) + CMD_BRIGHTNESS_SUFFIX
        await self._write(data)

    async def poll_state(self) -> TrickLedDeviceState:
        """Query the device for its current state.

        Sends the ``0xEF 0x01 0x77`` state-request command (``getLightData()``
        in the Android app) and waits for the notification response on
        :data:`~.const.BLE_CHAR_NOTIFY_UUID`.

        The device replies with an 8- or 12-byte packet whose first byte is
        ``0x66``.  Byte at index 2 indicates power state: ``0x23`` = ON,
        ``0x24`` = OFF.  For 12-byte responses bytes 6–8 carry R, G, B values.

        Returns:
            A :class:`~.models.TrickLedDeviceState` populated with the values
            read from the device.
        """
        from .models import TrickLedDeviceState  # local import to avoid circulars

        state = TrickLedDeviceState()
        event = asyncio.Event()

        def _notification_handler(_: int, data: bytearray) -> None:
            if len(data) >= 4 and data[0] == 0x66:
                state.is_on = data[2] == 0x23
                event.set()

        try:
            client = await self._connect()
            await client.start_notify(BLE_CHAR_NOTIFY_UUID, _notification_handler)
            try:
                await self._write(CMD_GET_STATE)
                async with asyncio.timeout(3.0):
                    await event.wait()
            finally:
                await client.stop_notify(BLE_CHAR_NOTIFY_UUID)
        except (BleakError, TimeoutError):
            _LOGGER.debug(
                "poll_state failed for %s, returning cached state",
                self._ble_device.address,
            )

        return state
