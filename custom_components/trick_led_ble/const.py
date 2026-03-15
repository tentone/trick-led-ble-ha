"""Constants for the Trick LED BLE integration."""

DOMAIN = "trick_led_ble"

# BLE device name prefixes used for discovery
# Devices advertise names matching "^Trick-" or "^Trick#"
BLE_NAME_PREFIXES: list[str] = ["Trick-", "Trick#"]

# Default name shown to the user when no device name is available
DEFAULT_NAME = "Trick LED"

# Manufacturer identifier
MANUFACTURER = "Trick LED"

# ── BLE GATT UUIDs ────────────────────────────────────────────────────────────
# Extracted from the decompiled Android application (DeviceUUID.java):
#   CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID
#   CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID
#   CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_DATA_UUID

# Primary service that carries LED control characteristics
BLE_SERVICE_UUID: str = "0000ffd5-0000-1000-8000-00805f9b34fb"

# Write characteristic used to send commands to the device
BLE_CHAR_WRITE_UUID: str = "0000ffd9-0000-1000-8000-00805f9b34fb"

# Notify/read characteristic used to receive state updates from the device
BLE_CHAR_NOTIFY_UUID: str = "0000ffd4-0000-1000-8000-00805f9b34fb"

# ── Command byte sequences ─────────────────────────────────────────────────────
# Extracted from MyBluetoothGatt.java (openLight / setColor methods).

# Command to turn the strip on  (openLight(true)  → {-52, 35, 51})
CMD_TURN_ON: bytes = b"\xcc\x23\x33"

# Command to turn the strip off  (openLight(false) → {-52, 36, 51})
CMD_TURN_OFF: bytes = b"\xcc\x24\x33"

# Brightness command format  (setColor(percent) → {86, brightness, 0, 0, 0, -16, -86})
# Full command: CMD_BRIGHTNESS_PREFIX + bytes([brightness_byte]) + CMD_BRIGHTNESS_SUFFIX
# brightness_byte: int in range 0–255  (percent * 2.55, capped at 255)
CMD_BRIGHTNESS_PREFIX: bytes = b"\x56"
CMD_BRIGHTNESS_SUFFIX: bytes = b"\x00\x00\x00\xf0\xaa"

# Query command to request the current device state  (getLightData() → {-17, 1, 119})
CMD_GET_STATE: bytes = b"\xef\x01\x77"

# ── Config entry data keys ─────────────────────────────────────────────────────
CONF_ADDRESS = "address"
CONF_NAME = "name"

# ── Coordinator ────────────────────────────────────────────────────────────────
POLLING_INTERVAL_SECONDS = 30
