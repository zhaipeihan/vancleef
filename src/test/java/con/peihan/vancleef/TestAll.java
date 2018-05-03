package con.peihan.vancleef;

import org.junit.Test;

public class TestAll {


    @Test
    public void testTime() throws Exception {
        System.out.println("1525358555");
        System.out.println(System.currentTimeMillis() / 1000);
    }

    @Test
    public void testSize() throws Exception {
        String ss = "3386d37d7b8e5279d9ee20d3a9607635e67fcd8edd58ca84c864464d49fcd94a";
        System.out.println(ss.length());
    }

}
