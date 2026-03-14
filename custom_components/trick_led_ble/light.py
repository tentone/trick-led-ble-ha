"""Light platform for the Trick LED BLE integration.

Exposes each Trick LED device as a single Home Assistant
:class:`~homeassistant.components.light.LightEntity`.

Because the physical device is a **monochromatic** LED strip only the
following features are supported:

* ``is_on`` / ``turn_on`` / ``turn_off``
* ``brightness`` (0–255)

Color-related attributes are intentionally omitted until the BLE protocol
is confirmed to support them.
"""

from __future__ import annotations

import logging
from typing import Any

from homeassistant.components.light import ATTR_BRIGHTNESS, ColorMode, LightEntity
from homeassistant.config_entries import ConfigEntry
from homeassistant.core import HomeAssistant, callback
from homeassistant.helpers.device_registry import DeviceInfo
from homeassistant.helpers.entity_platform import AddEntitiesCallback
from homeassistant.helpers.update_coordinator import CoordinatorEntity

from .const import DOMAIN, MANUFACTURER
from .coordinator import TrickLedCoordinator

_LOGGER = logging.getLogger(__name__)


async def async_setup_entry(
    hass: HomeAssistant,
    entry: ConfigEntry,
    async_add_entities: AddEntitiesCallback,
) -> None:
    """Set up Trick LED light entities from a config entry."""
    coordinator: TrickLedCoordinator = hass.data[DOMAIN][entry.entry_id]
    async_add_entities([TrickLedLight(coordinator, entry)])


class TrickLedLight(CoordinatorEntity[TrickLedCoordinator], LightEntity):
    """Representation of a Trick LED monochromatic strip as a HA light."""

    _attr_color_mode = ColorMode.BRIGHTNESS
    _attr_supported_color_modes = {ColorMode.BRIGHTNESS}
    _attr_has_entity_name = True
    _attr_name = None  # use device name as entity name

    def __init__(
        self,
        coordinator: TrickLedCoordinator,
        entry: ConfigEntry,
    ) -> None:
        super().__init__(coordinator)
        self._entry = entry
        self._attr_unique_id = coordinator.device_info.address

    # ── Device registry info ──────────────────────────────────────────────────

    @property
    def device_info(self) -> DeviceInfo:
        """Return device registry information for this entity."""
        info = self.coordinator.device_info
        return DeviceInfo(
            identifiers={(DOMAIN, info.address)},
            name=info.name,
            manufacturer=MANUFACTURER,
            model=info.model,
        )

    # ── State properties ──────────────────────────────────────────────────────

    @property
    def is_on(self) -> bool:
        """Return *True* when the LED strip is powered on."""
        return self.coordinator.device_info.state.is_on

    @property
    def brightness(self) -> int:
        """Return the current brightness (0–255)."""
        return self.coordinator.device_info.state.brightness

    # ── Commands ──────────────────────────────────────────────────────────────

    async def async_turn_on(self, **kwargs: Any) -> None:
        """Turn the LED strip on, optionally setting brightness."""
        ble = self.coordinator.ble_client

        brightness: int | None = kwargs.get(ATTR_BRIGHTNESS)

        if not self.is_on:
            await ble.turn_on()

        if brightness is not None:
            await ble.set_brightness(brightness)
            self.coordinator.device_info.state.brightness = brightness

        self.coordinator.device_info.state.is_on = True
        self.async_write_ha_state()

        # Trigger a coordinator refresh so all listeners get the new state
        await self.coordinator.async_request_refresh()

    async def async_turn_off(self, **kwargs: Any) -> None:
        """Turn the LED strip off."""
        await self.coordinator.ble_client.turn_off()
        self.coordinator.device_info.state.is_on = False
        self.async_write_ha_state()
        await self.coordinator.async_request_refresh()

    # ── Coordinator update callback ───────────────────────────────────────────

    @callback
    def _handle_coordinator_update(self) -> None:
        """React to state updates pushed by the coordinator."""
        self.async_write_ha_state()
