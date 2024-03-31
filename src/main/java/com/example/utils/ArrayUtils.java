package com.example.utils;

public class ArrayUtils {

    public static byte[] concat(byte[] a, byte[] b, int len) {
        byte[] c = new byte[a.length + len];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, len);
        return c;
    }


    public static String byte2HexString(byte[] b, int len) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sbf.append(String.format("%02X", b[i])).append("\t");
            if (i % 9 == 0 && i > 0)
                sbf.append("\r\n");
        }
        return sbf.toString();
    }

}
