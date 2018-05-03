package com.peihan.vancleef.util;


import com.peihan.vancleef.model.Block;
import org.apache.commons.codec.digest.DigestUtils;

public class MagicUtil {

    public static long getNowTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String hash(Block block) {
        if (block == null) {
            return "";
        }
        //将整个区块做hash
        String source = block.getIndex() + block.getPreviousHash() + block.getTimeStamp() + block.getData();
        return DigestUtils.sha256Hex(source);
    }

    public static String makeEmptyHashStr(){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0 ; i < 64 ; i ++){
            stringBuilder.append("0");
        }

        return stringBuilder.toString();
    }


}
