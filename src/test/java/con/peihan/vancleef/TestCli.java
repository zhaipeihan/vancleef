package con.peihan.vancleef;

import com.peihan.vancleef.App;
import com.peihan.vancleef.cli.Cli;
import com.peihan.vancleef.facade.BlockChainFacade;
import com.peihan.vancleef.facade.BlockChainFacadeImpl;
import org.junit.Test;

public class TestCli {

    @Test
    public void testinitCli() throws Exception {
        String[] args = {"vcf", "-init","peihan"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testListCli() throws Exception {
        String[] args = {"vcf","-list"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testAddCli() throws Exception {
        String[] args = {"vcf","-add","peihan"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testbalanceCli() throws Exception {
        String[] args = {"vcf","-balance","peihan"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testSendCli() throws Exception {
        BlockChainFacade blockChainFacade = new BlockChainFacadeImpl();
        blockChainFacade.transfer("lushu","peihan",3);
    }
}
