package com.example.websocket_verify;

public class MaskingTest {
    public static void main(String[] args) {
        byte[] mask = {(byte) 0xc8, (byte) 0xb1, (byte) 0xbc, (byte) 0xfe};
        byte[] masked = {(byte) 0xbc, (byte) 0xd4, (byte) 0xcf, (byte) 0x8a};
        String oriText = "test";

        byte[] decode = decode(mask, masked);
        System.out.println(new String(decode));
        byte[] oriData = oriText.getBytes();
        encode(oriData, mask);
        System.out.println(oriData);


    }


    private static byte[] decode(byte[] maskingKey, byte[] maskedData) {
        byte[] data = new byte[maskedData.length];
        for (int i = 0; i < maskedData.length; i++) {
            int j = i % 4;
            data[i] = (byte) (maskedData[i] ^ maskingKey[j]);
        }
        return  data;
    }


    private static void encode(byte[] oriData, byte[] maskingKey) {
        for (int i = 0; i < oriData.length; i++) {
            oriData[i] ^= maskingKey[i % 4];
        }
    }
}
