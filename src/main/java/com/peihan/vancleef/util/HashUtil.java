package com.peihan.vancleef.util;

import com.peihan.vancleef.model.Block;
import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {


    public static String hash(Block block) {
        if (block == null) {
            return "";
        }
        //将整个区块做hash
        String source = block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + block.getData() + String.valueOf(block.getNonce());
        return DigestUtils.sha256Hex(source);
    }

    public static String hashWithNonce(Block block, long nonce) {
        if (block == null) {
            return "";
        }
        String source = block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + block.getData() + String.valueOf(nonce);
        return DigestUtils.sha256Hex(source);
    }

    public static String hash(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return DigestUtils.sha256Hex(bytes);
    }


}
