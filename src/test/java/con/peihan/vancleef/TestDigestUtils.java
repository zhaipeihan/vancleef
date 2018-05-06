package con.peihan.vancleef;

import com.peihan.vancleef.util.MagicUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;


public class TestDigestUtils {


    @Test
    public void testSha256() throws Exception {

        String test1 = "Hello";
        long test2 = 4;

        String result1 = DigestUtils.sha256Hex(test1);
        String result2 = DigestUtils.sha256Hex(test1.getBytes());
        //String result3 = Hex.encodeHexString(DigestUtils.sha256(test1));
        //String result4 = Hex.encodeHexString(DigestUtils.sha256(test1.getBytes()));
        String result5 = DigestUtils.sha256Hex(test1 + test2);
        String result6 = DigestUtils.sha256Hex(MagicUtil.mergeBytes(test1.getBytes(), MagicUtil.longtoBytes(test2)));

        System.out.println(result1);
        System.out.println(result2);
        //System.out.println(result3);
        //System.out.println(result4);
        System.out.println(result5);
        System.out.println(result6);

        assert result1.equals(result2);
        //assert result3.equals(result4);
        //assert result1.equals(result3);

        long a = 1;
        long b = 2;
        System.out.println(a + b);
        System.out.println("" + a + b);

    }
}
