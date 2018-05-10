package con.peihan.vancleef;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import com.peihan.vancleef.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;

public class TestBlockChain {

    private static final Logger logger = LogManager.getLogger();

    @Test
    public void testBlockChain() throws Exception {
        BlockChain blockChain = new BlockChain();
        blockChain.createBlockChain("test");
        blockChain.addBlock(new ArrayList<>());
        BlockChain.BlockChainIterator blockChainIterator = blockChain.getBlockChainIterator();
        while (blockChainIterator.hasNext()){
            Block block = blockChainIterator.next();
            logger.info("当前区块信息为{}",block);
        }

    }
}
