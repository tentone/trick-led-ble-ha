package com.consmart.ble;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class AES {
    static String MOD = "AES/ECB/PKCS5Padding";
    static byte[] sKey = {-48, -7, -12, -116, 89, -94, 105, 29, 24, 83, -53, -38, -128, -124, 67, -109};

    public static byte[] Encrypt(byte[] bArr) throws Exception {
        byte[] bArr2 = sKey;
        if (bArr2 == null) {
            System.out.print("Key为空null");
            return null;
        }
        if (bArr2.length != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(sKey, "AES");
        Cipher cipher = Cipher.getInstance(MOD);
        cipher.init(1, secretKeySpec);
        return cipher.doFinal(bArr);
    }

    public static byte[] Decrypt(byte[] bArr) throws Exception {
        try {
            byte[] bArr2 = sKey;
            if (bArr2 == null) {
                System.out.print("Key为空null");
                return null;
            }
            if (bArr2.length != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance(MOD);
            cipher.init(2, secretKeySpec);
            try {
                return cipher.doFinal(bArr);
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception e2) {
            System.out.println(e2.toString());
            return null;
        }
    }

    public static void main(String[] strArr) throws Exception {
        byte[] bArr = {10, 11, 52, 12};
        System.out.println(bArr);
        byte[] bArrEncrypt = Encrypt(bArr);
        String str = "原数据加密后- ";
        if (bArrEncrypt != null) {
            for (byte b : bArrEncrypt) {
                str = String.valueOf(str) + Integer.toHexString(b & 255) + " ";
            }
        }
        System.out.println(str);
        byte[] bArrDecrypt = Decrypt(bArrEncrypt);
        String str2 = "解密后的-";
        if (bArrDecrypt != null) {
            for (byte b2 : bArrDecrypt) {
                str2 = String.valueOf(str2) + Integer.toHexString(b2 & 255) + " ";
            }
        }
        System.out.println(str2);
    }
}
