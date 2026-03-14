"""Data models for the Trick LED BLE integration."""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class TrickLedDeviceState:
    """Represents the last-known state of a Trick LED device.

    All fields are placeholders.  Extend / adjust once the actual BLE
    protocol is reverse-engineered from the decompiled application.
    """

    # Whether the LED strip is currently powered on
    is_on: bool = False

    # Current brightness level expressed as a value in 0–255.
    # 0 means off / minimum, 255 means full brightness.
    brightness: int = 255

    # Firmware / hardware version string reported by the device (if any)
    firmware_version: str = ""


@dataclass
class TrickLedDeviceInfo:
    """Static information about a discovered Trick LED device."""

    # BLE MAC address (e.g. "AA:BB:CC:DD:EE:FF")
    address: str = ""

    # Advertised device name (e.g. "Trick-001" or "Trick#A2")
    name: str = ""

    # Human-readable model string (placeholder – fill in once known)
    model: str = "Trick LED Controller"

    # Manufacturer string
    manufacturer: str = "Trick LED"

    # Most-recent RSSI reading (dBm); None when not available
    rssi: int | None = None

    # Mutable runtime state
    state: TrickLedDeviceState = field(default_factory=TrickLedDeviceState)
