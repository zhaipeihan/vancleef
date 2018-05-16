package com.peihan.vancleef.p2p;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NeighborNode {

    private String ip;

    private int port;
}
