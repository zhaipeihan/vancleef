package con.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;
import org.junit.Test;

public class TestCli {

    @Test
    public void testInitCli() throws Exception {
        String[] args = {"init", "-address", "peihan"};
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
        String[] args = {"balance", "-address", "peihan"};
        Cli cli = new Cli(args);
        cli.start();
    }

    @Test
    public void testSendCli() throws Exception {
        String[] args = {"send", "-from", "peihan", "-to", "lushu", "-amount", "5"};
        Cli cli = new Cli(args);
        cli.start();
    }


}
