package con.peihan.vancleef;

import com.peihan.vancleef.App;
import com.peihan.vancleef.cli.Cli;
import org.junit.Test;

public class TestCli {

    @Test
    public void testinitCli() throws Exception {
        String[] args = {"vcf", "-init"};
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
        String[] args = {"vcf","-add","tow block"};
        Cli cli = new Cli(args);
        cli.start();
    }
}
