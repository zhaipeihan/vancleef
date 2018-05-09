package con.peihan.vancleef;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.SerializeUtil;
import org.junit.Test;

import java.util.Arrays;

public class TestSerializeUtil {

    @Test
    public void testSerializeAndDeSerialize() throws Exception {
        Block block = new Block();
        block.setPreviousHash(MagicUtil.makeEmptyHashStr());
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setData("this is the genesis block");
        block.setNonce(0);
        block.setHash(HashUtil.hash(block));

        byte[] bytes = SerializeUtil.serialize(block);
        System.out.println(Arrays.toString(bytes));
        System.out.println(SerializeUtil.deSerialize(bytes));


    }
}
