package com.peihan.vancleef.config;

import com.peihan.vancleef.p2p.NeighborNode;
import lombok.Data;

import java.util.List;

@Data
public class Config {

    private int port;

    private List<String> ipPorts;

}
