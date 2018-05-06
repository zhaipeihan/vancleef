package con.peihan.vancleef;

import com.peihan.vancleef.model.BlockChain;
import org.junit.Test;

public class TestBlockChain {


    @Test
    public void testBlockChain() throws Exception {
        BlockChain blockChain = new BlockChain();
        blockChain.initBlockChain();
        blockChain.addBlock("Hello");
        blockChain.addBlock("world");
        if (!blockChain.isBlockChainValid()) {
            throw new Exception("区块链非法");
        }
        blockChain.showAllBlocks();
    }
}
