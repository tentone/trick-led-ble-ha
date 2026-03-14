"""Constants for the Trick LED BLE integration."""

DOMAIN = "trick_led_ble"

# BLE device name prefixes used for discovery
# Devices advertise names matching "^Trick-" or "^Trick#"
BLE_NAME_PREFIXES: list[str] = ["Trick-", "Trick#"]

# Default name shown to the user when no device name is available
DEFAULT_NAME = "Trick LED"

# Manufacturer identifier (placeholder – fill in once known)
MANUFACTURER = "Trick LED"

# ── BLE GATT UUIDs ────────────────────────────────────────────────────────────
# All UUIDs below are placeholders.  Replace with the actual values discovered
# from the decompiled application once they are available.

# Primary service that carries LED control characteristics
BLE_SERVICE_UUID: str = "0000XXXX-0000-1000-8000-00805f9b34fb"

# Write characteristic used to send commands to the device
BLE_CHAR_WRITE_UUID: str = "0000YYYY-0000-1000-8000-00805f9b34fb"

# Notify/read characteristic used to receive state updates from the device
BLE_CHAR_NOTIFY_UUID: str = "0000ZZZZ-0000-1000-8000-00805f9b34fb"

# ── Command byte sequences ─────────────────────────────────────────────────────
# All command payloads below are placeholders.  Replace with the actual bytes
# once they are reverse-engineered from the decompiled application.

# Command to turn the strip on
CMD_TURN_ON: bytes = b"\x00"

# Command to turn the strip off
CMD_TURN_OFF: bytes = b"\x00"

# Prefix bytes prepended to a brightness command.
# Usage: CMD_BRIGHTNESS_PREFIX + bytes([brightness_byte])
# brightness_byte: int in range 0–255
# TODO: replace the prefix with the actual command header byte(s)
CMD_BRIGHTNESS_PREFIX: bytes = b"\x00"

# ── Config entry data keys ─────────────────────────────────────────────────────
CONF_ADDRESS = "address"
CONF_NAME = "name"

# ── Coordinator ────────────────────────────────────────────────────────────────
POLLING_INTERVAL_SECONDS = 30
