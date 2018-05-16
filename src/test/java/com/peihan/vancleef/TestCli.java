package com.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;
import org.junit.Test;

public class TestCli {

    private static final String peihan = "12eDgHAaLJ7g6AgHf2GhFresSxbvFaeJBZ";
    private static final String lushu = "1J2PdRfKwsSxSbFm3rQs6ACFiZLmQGHaMR";
    public static final String A = "162ZbBPNemMFWsoNZeSLoSApBciZEadEoa";
    public static final String B = "1Hgb4z3oLCfvq45Qk4wxMfBNZqtJnHy2Ug";

    @Test
    public void testInitCli() throws Exception {
        String[] args = {"init", "-address", A};
        Cli cli = new Cli();
        cli.parse(args);
    }

    @Test
    public void testListCli() throws Exception {
        String[] args = {"list"};
        Cli cli = new Cli();
        cli.parse(args);
    }

    @Test
    public void testBalanceCli() throws Exception {
        String[] args = {"balance", "-address", B};
        Cli cli = new Cli();
        cli.parse(args);
    }

    @Test
    public void testSendCli() throws Exception {
        String[] args = {"send", "-from", A, "-to", B, "-amount", "5"};
        Cli cli = new Cli();
        cli.parse(args);
    }

    @Test
    public void testWalletCli() throws Exception {
        String[] args = {"wallet"};
        Cli cli = new Cli();
        cli.parse(args);
    }
}
