"""DataUpdateCoordinator for the Trick LED BLE integration."""

from __future__ import annotations

import logging
from datetime import timedelta

from homeassistant.core import HomeAssistant
from homeassistant.helpers.update_coordinator import DataUpdateCoordinator, UpdateFailed

from .bluetooth import TrickLedBleClient
from .const import DOMAIN, POLLING_INTERVAL_SECONDS
from .models import TrickLedDeviceInfo, TrickLedDeviceState

_LOGGER = logging.getLogger(__name__)


class TrickLedCoordinator(DataUpdateCoordinator[TrickLedDeviceState]):
    """Coordinates periodic state updates for a single Trick LED device.

    One coordinator instance is created per config entry (i.e. per physical
    device).  Platform entities subscribe to it via
    :meth:`~homeassistant.helpers.update_coordinator.CoordinatorEntity`.
    """

    def __init__(
        self,
        hass: HomeAssistant,
        device_info: TrickLedDeviceInfo,
        ble_client: TrickLedBleClient,
    ) -> None:
        """Initialise the coordinator.

        Args:
            hass: The Home Assistant instance.
            device_info: Static information about the physical device.
            ble_client: BLE client used to communicate with the device.
        """
        super().__init__(
            hass,
            _LOGGER,
            name=f"{DOMAIN}_{device_info.address}",
            update_interval=timedelta(seconds=POLLING_INTERVAL_SECONDS),
        )
        self.device_info = device_info
        self.ble_client = ble_client

    async def _async_update_data(self) -> TrickLedDeviceState:
        """Fetch the latest state from the device.

        Called automatically by the base class every *update_interval* seconds
        and also manually when an entity triggers a state refresh.

        Returns:
            The current :class:`~.models.TrickLedDeviceState`.

        Raises:
            UpdateFailed: Wraps any exception raised by the BLE client so that
                Home Assistant can handle the error gracefully.
        """
        try:
            state = await self.ble_client.poll_state()
        except Exception as exc:  # noqa: BLE001
            raise UpdateFailed(
                f"Error communicating with {self.device_info.address}: {exc}"
            ) from exc

        self.device_info.state = state
        return state
