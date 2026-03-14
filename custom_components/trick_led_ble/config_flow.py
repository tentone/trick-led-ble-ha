"""Config flow for the Trick LED BLE integration.

Supports two entry points:
1. **Bluetooth discovery** – Home Assistant detects an advertising device
   whose name matches one of the patterns in ``manifest.json``
   (``Trick-*`` / ``Trick#*``) and calls
   :meth:`TrickLedConfigFlow.async_step_bluetooth`.
2. **Manual setup** – The user opens *Settings → Devices & Services → Add
   Integration* and types in a BLE MAC address.
"""

from __future__ import annotations

import logging
import re
from typing import Any

import voluptuous as vol

from homeassistant.components.bluetooth import (
    BluetoothServiceInfoBleak,
    async_discovered_service_info,
)
from homeassistant.config_entries import ConfigFlow, ConfigFlowResult
from homeassistant.const import CONF_ADDRESS

from .const import BLE_NAME_PREFIXES, CONF_NAME, DEFAULT_NAME, DOMAIN

_LOGGER = logging.getLogger(__name__)

# Pre-compiled pattern that matches device names like "Trick-001" or "Trick#A2"
_DEVICE_NAME_RE = re.compile(r"^(Trick-|Trick#)")


def _is_trick_led_device(name: str) -> bool:
    """Return *True* if *name* looks like a Trick LED device."""
    return bool(_DEVICE_NAME_RE.match(name))


class TrickLedConfigFlow(ConfigFlow, domain=DOMAIN):
    """Handle a config flow for Trick LED BLE."""

    VERSION = 1

    def __init__(self) -> None:
        self._discovery_info: BluetoothServiceInfoBleak | None = None
        self._discovered_devices: dict[str, str] = {}  # address -> name

    # ── Bluetooth auto-discovery ──────────────────────────────────────────────

    async def async_step_bluetooth(
        self, discovery_info: BluetoothServiceInfoBleak
    ) -> ConfigFlowResult:
        """Handle a device discovered via Bluetooth.

        Called by Home Assistant when an advertising device matches the
        ``bluetooth`` patterns declared in ``manifest.json``.
        """
        _LOGGER.debug(
            "Bluetooth discovery: name=%s address=%s",
            discovery_info.name,
            discovery_info.address,
        )

        await self.async_set_unique_id(discovery_info.address)
        self._abort_if_unique_id_configured()

        self._discovery_info = discovery_info
        self.context["title_placeholders"] = {
            "name": discovery_info.name or DEFAULT_NAME,
        }
        return await self.async_step_bluetooth_confirm()

    async def async_step_bluetooth_confirm(
        self, user_input: dict[str, Any] | None = None
    ) -> ConfigFlowResult:
        """Ask the user to confirm a discovered device."""
        assert self._discovery_info is not None

        if user_input is not None:
            return self.async_create_entry(
                title=self._discovery_info.name or DEFAULT_NAME,
                data={
                    CONF_ADDRESS: self._discovery_info.address,
                    CONF_NAME: self._discovery_info.name or DEFAULT_NAME,
                },
            )

        return self.async_show_form(
            step_id="bluetooth_confirm",
            description_placeholders={
                "name": self._discovery_info.name or DEFAULT_NAME,
                "address": self._discovery_info.address,
            },
        )

    # ── Manual / user-initiated setup ─────────────────────────────────────────

    async def async_step_user(
        self, user_input: dict[str, Any] | None = None
    ) -> ConfigFlowResult:
        """Handle the initial user step.

        If previously-discovered devices are available the user is presented
        with a picker; otherwise they are asked to enter a MAC address
        directly.
        """
        if user_input is not None:
            address = user_input[CONF_ADDRESS].strip().upper()
            name = self._discovered_devices.get(address, DEFAULT_NAME)

            await self.async_set_unique_id(address, raise_on_progress=False)
            self._abort_if_unique_id_configured()

            return self.async_create_entry(
                title=name,
                data={CONF_ADDRESS: address, CONF_NAME: name},
            )

        # Collect already-discovered (but not yet configured) Trick LED devices
        current_addresses = self._async_current_ids()
        for service_info in async_discovered_service_info(self.hass, connectable=True):
            if (
                service_info.address not in current_addresses
                and _is_trick_led_device(service_info.name or "")
            ):
                self._discovered_devices[service_info.address] = (
                    service_info.name or DEFAULT_NAME
                )

        if self._discovered_devices:
            # Let the user pick from a list of nearby devices
            schema = vol.Schema(
                {
                    vol.Required(CONF_ADDRESS): vol.In(
                        {
                            addr: f"{name} ({addr})"
                            for addr, name in self._discovered_devices.items()
                        }
                    )
                }
            )
        else:
            # Fall back to free-text address entry
            schema = vol.Schema(
                {
                    vol.Required(CONF_ADDRESS): str,
                }
            )

        return self.async_show_form(step_id="user", data_schema=schema)
