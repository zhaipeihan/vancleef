package com.peihan.vancleef.util;

import java.nio.ByteBuffer;

public class MagicUtil {

    public static long getNowTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String makeEmptyHashStr() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 64; i++) {
            stringBuilder.append("0");
        }

        return stringBuilder.toString();
    }


    public static byte[] mergeBytes(byte[]... bytes) {

        int totalLength = 0;

        for (byte[] bytess : bytes) {
            totalLength += bytess.length;
        }

        byte[] merge = new byte[totalLength];

        int destPos = 0;

        for (byte[] bytess : bytes) {
            System.arraycopy(bytess, 0, merge, destPos, bytess.length);
            destPos += bytess.length;
        }
        return merge;
    }


    public static byte[] longtoBytes(long val) {
        return ByteBuffer.allocate(Long.BYTES).putLong(val).array();
    }


    public static String repeatStr(String str, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            buf.append(str);
        }
        return buf.toString();
    }


}
