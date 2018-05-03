package con.peihan.vancleef;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.DataInputStream;
import java.util.Arrays;

public class TestDigestUtils {


    @Test
    public void testSha256() throws Exception {

        String test = "Hello";

        String result1 = DigestUtils.sha256Hex(test);
        String result2 = DigestUtils.sha256Hex(test.getBytes());
        String result3 = Hex.encodeHexString(DigestUtils.sha256(test));
        String result4 = Hex.encodeHexString(DigestUtils.sha256(test.getBytes()));


        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);

        assert result1.equals(result2);
        assert result3.equals(result4);
        assert result1.equals(result3);

    }
}
