package com.peihan.vancleef.util;

import com.peihan.vancleef.p2p.NeighborNode;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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


    public static Object cloneCopy(Object object) {
        return SerializeUtil.deSerialize(SerializeUtil.serialize(object));
    }


    public static Set<NeighborNode> getNeighborNodes(List<String> ipPorts) {
        return ipPorts.stream().map(ipp -> {
            String[] data = ipp.split(":");
            return new NeighborNode(data[0],Integer.valueOf(data[1]));
        }).collect(Collectors.toSet());
    }


}
