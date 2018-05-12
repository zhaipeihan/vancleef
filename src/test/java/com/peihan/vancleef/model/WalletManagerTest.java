package com.peihan.vancleef.model;

import com.peihan.vancleef.util.WalletUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class WalletManagerTest {

    @Test
    public void getInstance() throws Exception {
        WalletManager walletManager = WalletManager.getInstance();
        walletManager.init();
    }

    @Test
    public void init() throws Exception {
    }

    @Test
    public void addWallet() throws Exception {
        WalletManager walletManager = WalletManager.getInstance();
        walletManager.init();
        Wallet wallet = new Wallet();
        String address = WalletUtil.getAddress(wallet);
        walletManager.addWallet(address,wallet);
    }

    @Test
    public void getWallet() throws Exception {



    }

    @Test
    public void name() throws Exception {


        Wallet wallet = new Wallet();
        String address = WalletUtil.getAddress(wallet);


        WalletManager walletManager = WalletManager.getInstance();
        walletManager.init();
        walletManager.addWallet(address,wallet);
        walletManager.init();
        Wallet wallet2 = walletManager.getWallet(address);


        assert wallet.equals(wallet2);

        assert wallet.getPrivateKey().equals(wallet2.getPrivateKey());
    }
}