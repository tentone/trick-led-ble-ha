# trick-led-ble-ha

Home Assistant custom integration for **Trick LED** BLE monochromatic LED strip controllers.

Devices advertise over Bluetooth with names matching `Trick-*` or `Trick#*`.

---

## Features

- Automatic BLE device discovery (via HA Bluetooth integration)
- Manual setup via MAC address
- Monochromatic light entity with **on/off** and **brightness** control
- Periodic state polling with `DataUpdateCoordinator`

> **Note:** The low-level BLE command protocol (GATT UUIDs and command byte
> sequences) is not yet implemented.  All values in `const.py` and the method
> bodies in `bluetooth.py` are **placeholders** that will be filled in once
> the decompiled Android application has been analysed.

---

## Installation

### HACS (recommended)

1. Add this repository as a custom repository in HACS.
2. Search for **Trick LED BLE** and install it.
3. Restart Home Assistant.

### Manual

1. Copy the `custom_components/trick_led_ble` directory into your HA
   `config/custom_components/` folder.
2. Restart Home Assistant.

---

## Configuration

Once installed, go to **Settings → Devices & Services → Add Integration** and
search for **Trick LED BLE**.

- If a device is already advertising nearby it will be offered automatically.
- Otherwise enter the device's Bluetooth MAC address manually.

---

## Project structure

```
custom_components/trick_led_ble/
├── __init__.py          # Integration setup / teardown
├── manifest.json        # Integration metadata & BLE discovery patterns
├── const.py             # Domain, BLE UUIDs, command bytes (placeholders)
├── models.py            # Device state & info dataclasses
├── bluetooth.py         # BLE communication layer (placeholders)
├── coordinator.py       # DataUpdateCoordinator (periodic polling)
├── config_flow.py       # UI config flow (auto-discovery + manual)
├── light.py             # LightEntity (brightness only)
├── strings.json         # UI string keys
└── translations/
    └── en.json          # English UI strings
```

---

## License

MIT – see [LICENSE](LICENSE).
