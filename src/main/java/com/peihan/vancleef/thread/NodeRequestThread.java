package com.peihan.vancleef.thread;

import com.peihan.vancleef.p2p.Node;

public class NodeRequestThread implements Runnable {
    private Node node;

    public NodeRequestThread(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        node.requestAllBlock();
    }
}
