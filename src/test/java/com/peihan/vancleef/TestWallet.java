package com.peihan.vancleef;

import com.peihan.vancleef.model.Wallet;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.WalletUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

public class TestWallet {


    @Test
    public void testAddress() throws Exception {
        Wallet wallet = new Wallet();

        String address1 = WalletUtil.getAddress(wallet.getPublicKey());
        String address3 = WalletUtil.getAddress(wallet.getPublicKey());

        System.out.println(address1);
        System.out.println(address3);
        assert Objects.equals(address1, address3);
    }


    @Test
    public void testDoubleHash() throws Exception {

        byte[] data = "Hello,World".getBytes();


        byte[] hash2 = HashUtil.doubleHash(data);

        System.out.println(Arrays.toString(hash2));
    }
}
