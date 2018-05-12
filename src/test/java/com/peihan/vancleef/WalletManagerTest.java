package com.peihan.vancleef;

import com.peihan.vancleef.model.Wallet;
import com.peihan.vancleef.model.WalletManager;
import com.peihan.vancleef.util.WalletUtil;
import org.junit.Test;

public class WalletManagerTest {

    @Test
    public void getInstance() throws Exception {
        WalletManager walletManager = WalletManager.getInstance();
    }

    @Test
    public void init() throws Exception {
    }

    @Test
    public void addWallet() throws Exception {
        WalletManager walletManager = WalletManager.getInstance();
        Wallet wallet = new Wallet();
        String address = WalletUtil.getAddress(wallet);
        walletManager.addWallet(address, wallet);
    }

    @Test
    public void getWallet() throws Exception {


    }

    @Test
    public void name() throws Exception {


        Wallet wallet = new Wallet();
        String address = WalletUtil.getAddress(wallet);


        WalletManager walletManager = WalletManager.getInstance();
        walletManager.addWallet(address, wallet);
        Wallet wallet2 = walletManager.getWallet(address);


        assert wallet.equals(wallet2);

        assert wallet.getPrivateKey().equals(wallet2.getPrivateKey());
    }


    @Test
    public void testWalletManager() throws Exception {

        String aaaa = "13FgBMAXjcYcDbiDv12V2mTAGoa2VnzjAm";
        WalletManager walletManager = WalletManager.getInstance();

        String address = walletManager.createWallet();
        System.out.println(address);
    }


    @Test
    public void testGetAll() throws Exception {
        WalletManager walletManager = WalletManager.getInstance();
        System.out.println(walletManager.getAllWallets());


    }
}