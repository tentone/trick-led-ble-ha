"""The Trick LED BLE integration."""

from __future__ import annotations

import logging

from bleak.backends.device import BLEDevice

from homeassistant.components.bluetooth import async_ble_device_from_address
from homeassistant.config_entries import ConfigEntry
from homeassistant.const import CONF_ADDRESS, Platform
from homeassistant.core import HomeAssistant
from homeassistant.exceptions import ConfigEntryNotReady

from .bluetooth import TrickLedBleClient
from .const import CONF_NAME, DEFAULT_NAME, DOMAIN
from .coordinator import TrickLedCoordinator
from .models import TrickLedDeviceInfo

_LOGGER = logging.getLogger(__name__)

PLATFORMS: list[Platform] = [Platform.LIGHT]


async def async_setup_entry(hass: HomeAssistant, entry: ConfigEntry) -> bool:
    """Set up Trick LED BLE from a config entry.

    Called by Home Assistant once for each config entry (device).

    Steps:
    1. Resolve the BLE device object from the stored MAC address.
    2. Build a :class:`~.bluetooth.TrickLedBleClient` for low-level comms.
    3. Create a :class:`~.coordinator.TrickLedCoordinator` and do an initial
       data fetch so we know the device is reachable before finishing setup.
    4. Store the coordinator in ``hass.data`` so platform modules can access
       it during entity registration.
    5. Forward setup to all declared platforms (currently only ``light``).
    """
    address: str = entry.data[CONF_ADDRESS]
    name: str = entry.data.get(CONF_NAME, DEFAULT_NAME)

    # Resolve the BLEDevice object from the address stored in the config entry
    ble_device: BLEDevice | None = async_ble_device_from_address(
        hass, address, connectable=True
    )
    if ble_device is None:
        raise ConfigEntryNotReady(
            f"Trick LED device {address!r} is not reachable via Bluetooth"
        )

    device_info = TrickLedDeviceInfo(address=address, name=name)
    ble_client = TrickLedBleClient(ble_device)
    coordinator = TrickLedCoordinator(hass, device_info, ble_client)

    # Perform first refresh – raises ConfigEntryNotReady on failure so HA
    # will retry until the device comes online
    await coordinator.async_config_entry_first_refresh()

    # Subscribe to persistent BLE notifications so state changes triggered by
    # a physical remote are reflected in Home Assistant immediately.
    await coordinator.async_start_notifications()

    hass.data.setdefault(DOMAIN, {})[entry.entry_id] = coordinator

    await hass.config_entries.async_forward_entry_setups(entry, PLATFORMS)
    return True


async def async_unload_entry(hass: HomeAssistant, entry: ConfigEntry) -> bool:
    """Unload a config entry.

    Tears down all platform entities and disconnects from the BLE device.
    """
    unloaded = await hass.config_entries.async_unload_platforms(entry, PLATFORMS)

    if unloaded:
        coordinator: TrickLedCoordinator = hass.data[DOMAIN].pop(entry.entry_id)
        await coordinator.ble_client.stop_notifications()
        await coordinator.ble_client.disconnect()

    return unloaded
