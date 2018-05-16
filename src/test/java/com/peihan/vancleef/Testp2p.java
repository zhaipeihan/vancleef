package com.peihan.vancleef;

import com.peihan.vancleef.p2p.NeighborNode;
import com.peihan.vancleef.p2p.Node;
import com.peihan.vancleef.qotm.Client;
import com.peihan.vancleef.qotm.Server;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class Testp2p {

    private static Set<NeighborNode> neighborNodeSet;


    static {
        neighborNodeSet = new HashSet<>(1);
        neighborNodeSet.add(new NeighborNode("127.0.0.1",8888));
    }


    @Test
    public void server() throws Exception {
        Node node = new Node(8888,neighborNodeSet);
        node.start();
    }

    @Test
    public void client() throws Exception {
        Node node = new Node(8888,neighborNodeSet);
        node.requestAllBlock();
    }



}
