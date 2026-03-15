# trick-led-ble-ha

Home Assistant custom integration for **Trick LED** BLE monochromatic LED strip controllers.

Devices advertise over Bluetooth with names matching `Trick-*` or `Trick#*`.

---

## Features

- Automatic BLE device discovery (via HA Bluetooth integration)
- Manual setup via MAC address
- Monochromatic light entity with **on/off** and **brightness** control
- Periodic state polling with `DataUpdateCoordinator`

---

## Prerequisites

- **Home Assistant** 2023.6 or later (requires `bluetooth` core integration)
- A Bluetooth adapter accessible to Home Assistant (built-in, USB dongle, or
  a remote Bluetooth proxy such as [ESPHome Bluetooth Proxy](https://esphome.io/components/bluetooth_proxy.html))
- A supported **Trick LED** BLE controller (device name starts with `Trick-`
  or `Trick#`)

---

## Installation

### HACS (recommended)

[HACS](https://hacs.xyz/) (Home Assistant Community Store) is the easiest way
to install and keep the integration up to date.

1. Open HACS in your Home Assistant sidebar.
2. Go to **Integrations**.
3. Click the **⋮** menu (top-right) and choose **Custom repositories**.
4. Paste `https://github.com/tentone/trick-led-ble-ha` in the *Repository*
   field, select **Integration** as the category and click **Add**.
5. Close the dialog, then search for **Trick LED BLE** in the HACS integrations
   list and click **Download**.
6. Restart Home Assistant.

### Manual

1. Download or clone this repository.
2. Copy the `custom_components/trick_led_ble` directory into your Home Assistant
   configuration folder so that the final path looks like:

   ```
   config/
   └── custom_components/
       └── trick_led_ble/
           ├── __init__.py
           ├── bluetooth.py
           ├── config_flow.py
           ├── const.py
           ├── coordinator.py
           ├── light.py
           ├── manifest.json
           ├── models.py
           ├── strings.json
           └── translations/
               └── en.json
   ```

3. Restart Home Assistant.

---

## Configuration

### Automatic discovery

When a Trick LED device is advertising nearby, Home Assistant will detect it
automatically and show a notification in **Settings → Devices & Services**.
Click **Configure** and confirm the device to complete setup.

### Manual setup

1. Go to **Settings → Devices & Services**.
2. Click **+ Add Integration** (bottom-right).
3. Search for **Trick LED BLE** and select it.
4. If any Trick LED devices were already detected nearby they will be listed for
   selection. Otherwise, enter the Bluetooth MAC address of your device (format
   `AA:BB:CC:DD:EE:FF`) and click **Submit**.

Once configured the integration creates a **Light** entity for the device.  Use
it to turn the strip on/off and to control brightness from any Home Assistant
dashboard, automation, or script.

---

## BLE Protocol

The low-level protocol was reverse-engineered from the official Android
application.  All communication uses the following GATT attributes:

| Attribute | UUID |
|-----------|------|
| Primary service | `0000ffd5-0000-1000-8000-00805f9b34fb` |
| Write characteristic | `0000ffd9-0000-1000-8000-00805f9b34fb` |
| Notify characteristic | `0000ffd4-0000-1000-8000-00805f9b34fb` |

Command byte sequences:

| Action | Bytes (hex) |
|--------|-------------|
| Turn on | `CC 23 33` |
| Turn off | `CC 24 33` |
| Set brightness (0–255) | `56 <brightness> 00 00 00 F0 AA` |
| Query state | `EF 01 77` |

The device responds to state queries with an 8- or 12-byte packet starting
with `0x66`.  Byte 2 indicates power state (`0x23` = ON, `0x24` = OFF).

---

## Troubleshooting

| Symptom | Possible cause & fix |
|---------|----------------------|
| Device not discovered automatically | Make sure the strip is powered on and within Bluetooth range. Check that the HA Bluetooth integration is enabled and a Bluetooth adapter is configured. |
| "Device is not reachable" on setup | The device went out of range or is off. Power it on and retry. |
| Brightness changes don't take effect | Some hardware revisions may behave differently. Open an issue with your device model and firmware version. |
| Integration disappears after HA restart | If the device is out of range at startup HA marks the entry as unavailable and retries automatically once the device is seen again. |

If you encounter a problem not listed above, please [open an issue](https://github.com/tentone/trick-led-ble-ha/issues) and include your Home Assistant logs (filtered to `trick_led_ble`).

---

## Project structure

```
custom_components/trick_led_ble/
├── __init__.py          # Integration setup / teardown
├── manifest.json        # Integration metadata & BLE discovery patterns
├── const.py             # Domain, BLE UUIDs, command bytes
├── models.py            # Device state & info dataclasses
├── bluetooth.py         # BLE communication layer
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
