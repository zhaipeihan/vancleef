package com.peihan.vancleef.util;


import com.peihan.vancleef.model.Block;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;

public class MagicUtil {

    public static long getNowTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String hash(Block block) {
        if (block == null) {
            return "";
        }
        //将整个区块做hash
        String source = String.valueOf(block.getIndex()) + block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + block.getData() + String.valueOf(block.getNonce());
        return DigestUtils.sha256Hex(source);
    }

    public static String hashWithNonce(Block block, long nonce) {
        if (block == null) {
            return "";
        }
        String source = String.valueOf(block.getIndex()) + block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + block.getData() + String.valueOf(nonce);
        return DigestUtils.sha256Hex(source);
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
