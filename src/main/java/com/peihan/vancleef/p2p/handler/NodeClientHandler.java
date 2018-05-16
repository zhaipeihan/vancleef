package com.peihan.vancleef.p2p.handler;

import com.peihan.vancleef.cache.NodeBlockCache;
import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.util.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        System.out.println("从节点拿到的数据为" + Arrays.toString(req));
        List<Block> blocks = (ArrayList<Block>) SerializeUtil.deSerialize(req);
        System.out.println("区块数据为" + blocks.toString());
        NodeBlockCache.getInstance().put(msg.sender().toString(),blocks);
        ctx.close();
    }
}
