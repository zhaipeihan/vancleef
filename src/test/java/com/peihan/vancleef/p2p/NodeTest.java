package com.peihan.vancleef.p2p;

import com.peihan.vancleef.model.MerkleTree;
import com.peihan.vancleef.thread.NodeRequestThread;
import com.peihan.vancleef.thread.NodeStartThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class NodeTest {

    private static Set<NeighborNode> neighborNodeSet;


    private Logger logger = LogManager.getLogger();


    static {
        neighborNodeSet = new HashSet<>(1);
        neighborNodeSet.add(new NeighborNode("127.0.0.1", 8888));
    }


    @Test
    public void name3() throws Exception {
        Node node = new Node(8888, neighborNodeSet);
        Thread t1 = new Thread(new NodeStartThread(node));
        Thread t2 = new Thread(new NodeRequestThread(node));

        t1.start();
        Thread.sleep(3000);
        t2.start();
        t2.join();
        logger.info("end");
    }


    @Test
    public void server() throws Exception {
        Node node = new Node(8888, neighborNodeSet);
        node.start();
    }

    @Test
    public void client() throws Exception {
        Node node = new Node(8888, neighborNodeSet);
        node.requestAllBlock();
    }
}