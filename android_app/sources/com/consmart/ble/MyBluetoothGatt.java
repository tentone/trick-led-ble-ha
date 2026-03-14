package com.consmart.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.qh.tools.DeviceUUID;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class MyBluetoothGatt {
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SREVICE_UPDATA = 5;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public BluetoothAdapter bluetoothAdapter;
    public Context context;
    public BleController mBleController;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mLEdevice;
    public MyBluetoothGattCallback mMyBluetoothGattCallback;
    public Resources mResources;
    private boolean isCheck = false;
    private byte[] checkdata = {1, 1, 1, 1};
    public int mConnectionState = 0;
    public MyBluetoothGatt mMyBluetoothGatt = this;
    private Handler mHandler = new Handler();
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() { // from class: com.consmart.ble.MyBluetoothGatt.1
        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            MyBluetoothGatt.this.mConnectionState = i;
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onConnectionStateChange(MyBluetoothGatt.this.mMyBluetoothGatt, i, i2);
            }
            if (i2 == 2) {
                bluetoothGatt.discoverServices();
            }
            if (i2 == 0) {
                MyBluetoothGatt.this.mBleController.removeMyBluetoothGatt(bluetoothGatt.getDevice().getAddress());
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onServicesDiscovered(MyBluetoothGatt.this.mMyBluetoothGatt, i);
            }
            if (bluetoothGatt != null) {
                MyBluetoothGatt.this.mBluetoothGatt = bluetoothGatt;
            }
            if (i == 0) {
                MyBluetoothGatt.this.mHandler.postDelayed(new Runnable() { // from class: com.consmart.ble.MyBluetoothGatt.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.e("", "--->>设置验证");
                        MyBluetoothGatt.this.setCharacteristicNotify("0000fff0-0000-1000-8000-00805f9b34fb", DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID);
                    }
                }, 100L);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onDescriptorWrite(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattDescriptor, i);
            }
            Log.e("", "descriptor -" + bluetoothGattDescriptor.getCharacteristic().getUuid().toString());
            if (i == 0 && DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID.equalsIgnoreCase(bluetoothGattDescriptor.getCharacteristic().getUuid().toString())) {
                MyBluetoothGatt.this.check();
            }
            super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            byte[] value;
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicChanged(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic);
            }
            if (DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_MUSICCHECK_UUID.equalsIgnoreCase(bluetoothGattCharacteristic.getUuid().toString()) && (value = bluetoothGattCharacteristic.getValue()) != null && value.length == 18 && 102 == (value[0] & 255) && 187 == (value[17] & 255) && value[1] == MyBluetoothGatt.this.checkdata[0] && value[2] == MyBluetoothGatt.this.checkdata[1] && value[3] == MyBluetoothGatt.this.checkdata[2] && value[4] == MyBluetoothGatt.this.checkdata[3]) {
                MyBluetoothGatt.this.isCheck = true;
                Log.e("", "验证成功！");
                if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                    MyBluetoothGatt.this.mMyBluetoothGattCallback.oncheck();
                }
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicWrite(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic, i);
            }
            super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onCharacteristicRead(MyBluetoothGatt.this.mMyBluetoothGatt, bluetoothGattCharacteristic, i);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
            if (MyBluetoothGatt.this.mMyBluetoothGattCallback != null) {
                MyBluetoothGatt.this.mMyBluetoothGattCallback.onReadRemoteRssi(MyBluetoothGatt.this.mMyBluetoothGatt, i, i2);
            }
        }
    };

    public void setMyBluetoothGattCallback(MyBluetoothGattCallback myBluetoothGattCallback) {
        this.mMyBluetoothGattCallback = myBluetoothGattCallback;
    }

    public MyBluetoothGatt(Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.mBleController = BleController.initialization(context);
    }

    private BluetoothGattCallback getmGattCallback() {
        return this.mGattCallback;
    }

    public BluetoothDevice getmLEdevice() {
        return this.mLEdevice;
    }

    public void connectGatt(String str) {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter == null) {
            return;
        }
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(str);
        this.mLEdevice = remoteDevice;
        this.mBluetoothGatt = remoteDevice.connectGatt(this.context, false, this.mGattCallback);
    }

    public void stopLEService() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
    }

    public ArrayList<BluetoothGattService> getBluetoothGattService() {
        ArrayList<BluetoothGattService> arrayList = new ArrayList<>();
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        return bluetoothGatt != null ? (ArrayList) bluetoothGatt.getServices() : arrayList;
    }

    public BluetoothGattService getService(UUID uuid) {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            return bluetoothGatt.getService(uuid);
        }
        return null;
    }

    public boolean readRSSI() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return false;
        }
        return bluetoothGatt.readRemoteRssi();
    }

    private void setMsg(String str, int i) {
        Message message = new Message();
        message.what = i;
        Bundle bundle = new Bundle();
        bundle.putString("deviceAddr", str);
        message.setData(bundle);
        this.mHandler.sendMessage(message);
    }

    public synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr) {
        if (!this.isCheck) {
            return false;
        }
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return false;
        }
        characteristic.setWriteType(2);
        characteristic.setValue(bArr);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr, boolean z) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return false;
        }
        characteristic.setWriteType(2);
        characteristic.setValue(bArr);
        return this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public synchronized boolean readCharacteristic(String str, String str2) {
        if (!this.isCheck) {
            return false;
        }
        if (this.mBluetoothGatt == null) {
            return false;
        }
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGattService service = this.mBluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return false;
        }
        return this.mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean setCharacteristicNotify(String str, String str2, boolean z) {
        BluetoothGattService service;
        if (!this.isCheck) {
            return false;
        }
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, z);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setCharacteristicNotify(String str, String str2) {
        BluetoothGattService service;
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor == null) {
            return false;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void check() {
        byte[] bArr;
        Log.e("", "验证.....");
        Random random = new Random();
        byte[] bArr2 = {-86, -81, -86, 70, -21, 28, -21, 15, -7, 68, 73, 118, 53, -42, 123, 64, 4, 85};
        int i = 0;
        while (true) {
            bArr = this.checkdata;
            if (i < bArr.length) {
                int iNextInt = random.nextInt(200);
                if (iNextInt == 0) {
                    iNextInt = 1;
                }
                this.checkdata[i] = (byte) (iNextInt & 255);
                i++;
            } else {
                try {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        byte[] bArrEncrypt = AES.Encrypt(bArr);
        int i2 = 0;
        while (i2 < bArrEncrypt.length) {
            int i3 = i2 + 1;
            bArr2[i3] = bArrEncrypt[i2];
            i2 = i3;
        }
        writeCharacteristic("0000fff0-0000-1000-8000-00805f9b34fb", "0000fff5-0000-1000-8000-00805f9b34fb", bArr2, false);
    }
}
