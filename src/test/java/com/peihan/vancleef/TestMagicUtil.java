package com.peihan.vancleef;

import com.peihan.vancleef.util.MagicUtil;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TestMagicUtil {


    @Test
    public void testMergeBytes() throws Exception {
        byte[] bytes1 = new byte[]{0,1};
        byte[] bytes2 = new byte[]{2,3};
        byte[] bytes3 = new byte[]{4,5};

        byte[] merge = MagicUtil.mergeBytes(bytes1,bytes2,bytes3);

        System.out.println(Arrays.toString(merge));
    }
}
