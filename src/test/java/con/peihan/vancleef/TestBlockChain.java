package con.peihan.vancleef;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import com.peihan.vancleef.util.StorageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestBlockChain {

    private static final Logger logger = LogManager.getLogger();

    @Test
    public void testBlockChain() throws Exception {
        BlockChain blockChain = new BlockChain();
        blockChain.initBlockChain();
        String lastHash = StorageUtil.getInstance().getLastBlockHash();
        logger.info("当前最后一个区块hash为{}", lastHash);
        Block block = StorageUtil.getInstance().getBlock(lastHash);
        logger.info("最后一个区块信息为{}", block);
    }
}
