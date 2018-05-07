package con.peihan.vancleef;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestBlockChain {

    private static final Logger logger = LogManager.getLogger();

    @Test
    public void testBlockChain() throws Exception {
        BlockChain blockChain = new BlockChain();
        blockChain.initBlockChain();
        blockChain.addBlock("hello");
        blockChain.addBlock("world");
        BlockChain.BlockChainIterator blockChainIterator = blockChain.getBlockChainIterator();
        while (blockChainIterator.hasNext()){
            Block block = blockChainIterator.next();
            logger.info("当前区块信息为{}",block);
        }

    }
}
