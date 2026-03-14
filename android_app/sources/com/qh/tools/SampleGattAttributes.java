package com.qh.tools;

import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class SampleGattAttributes {
    public static String CLIENT_CHARACTERISTIC_CONFIG;
    public static String HEART_RATE_MEASUREMENT;
    public static String HEART_RATE_MEASUREMENT2;
    private static HashMap<String, String> attributes;

    static {
        HashMap<String, String> map = new HashMap<>();
        attributes = map;
        HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
        HEART_RATE_MEASUREMENT2 = "0000ffe2-0000-1000-8000-00805f9b34fb";
        CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
        map.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(DeviceUUID.SLIC_BLE_READ_CHARACTERISTICS_DEVICE_INFO_MANUFACTURER_NAME_UUID, "Manufacturer Name String");
    }

    public static String lookup(String str, String str2) {
        String str3 = attributes.get(str);
        return str3 == null ? str2 : str3;
    }
}
