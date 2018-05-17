package com.peihan.vancleef;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import com.peihan.vancleef.model.TxOutput;
import com.peihan.vancleef.model.Wallet;
import com.peihan.vancleef.util.SerializeUtil;
import com.peihan.vancleef.util.StorageUtil;
import com.peihan.vancleef.util.WalletUtil;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestStorge {

    @Test
    public void name() throws Exception {
/*
        StorageUtil storageUtil = StorageUtil.getInstance();


        BlockChain blockChain = BlockChain.getInstance();


        List<Block> blocks = blockChain.getAllBlocks();


        Block block = blocks.get(1);


        String txId = block.getTransactions().get(0).getTxId();
        List<TxOutput> txOutputs = block.getTransactions().get(0).getTxOutputs();
        storageUtil.putUXTOs(txId,txOutputs);

        List<TxOutput> getUXTOs = storageUtil.getUXTOs(txId);

        assert CollectionUtils.isEqualCollection(txOutputs,getUXTOs);*/
    }
}
