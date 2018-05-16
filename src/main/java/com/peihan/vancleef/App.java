package com.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;
import com.peihan.vancleef.config.Config;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.p2p.NeighborNode;
import com.peihan.vancleef.p2p.Node;
import com.peihan.vancleef.thread.NodeStartThread;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.YamlUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class App {

    //main线程负责ui交互，NodeServer线程负责响应网络中其他结点的请求
    public static void main(String[] args) throws ServiceException {
        Cli cli = new Cli();
        //启动节点
        new Thread(new NodeStartThread(Node.getInstance())).start();

        //主线程负责ui交互
        cli.ui();
    }


}
