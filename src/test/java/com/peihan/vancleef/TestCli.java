package com.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;
import org.junit.Test;

public class TestCli {

    private static final String peihan = "12eDgHAaLJ7g6AgHf2GhFresSxbvFaeJBZ";

    private static final String lushu = "1J2PdRfKwsSxSbFm3rQs6ACFiZLmQGHaMR";


    @Test
    public void testInitCli() throws Exception {
        String[] args = {"init", "-address", peihan};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testListCli() throws Exception {
        String[] args = {"list"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testBalanceCli() throws Exception {
        String[] args = {"balance", "-address", peihan};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testSendCli() throws Exception {
        String[] args = {"send", "-from", peihan, "-to", lushu, "-amount", "1"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testWalletCli() throws Exception {
        String[] args = {"wallet"};
        Cli cli = new Cli(args);
        cli.start();
    }
}
