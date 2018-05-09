package com.peihan.vancleef.action;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.MagicUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pow {

    private static final Logger logger = LogManager.getLogger();


    /**
     * pow的难度值
     * 代表hash前缀0的个数
     * 默认使用前缀4个0
     */
    private static int DIFFICULTY = 4;


    public static void pow(Block block) {

        long nonce = 0;

        long startTime = System.currentTimeMillis();
        while (!isHashValid(HashUtil.hashWithNonce(block, nonce))) {
            nonce++;
        }
        long endTime = System.currentTimeMillis();

        //工作量证明通过之后写入nonce和hash
        block.setNonce(nonce);
        block.setHash(HashUtil.hash(block));

        logger.info("当前正在执行pow的区块信息为{},pow执行时间为{}ms", block, endTime - startTime);

    }

    //重设难度值
    public static void resetDifficulty(int difficulty) {
        Pow.DIFFICULTY = difficulty;
    }

    public static boolean isHashValid(String hash) {
        String prefix = MagicUtil.repeatStr("0", Pow.DIFFICULTY);
        return hash.startsWith(prefix);
    }

}
