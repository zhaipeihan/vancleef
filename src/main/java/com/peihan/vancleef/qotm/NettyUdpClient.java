package com.peihan.vancleef.qotm;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * Created by smzdm on 16/8/10.
 */
public class NettyUdpClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());

            Channel ch = b.bind(0).sync().channel();
            // 向网段类所有机器广播发UDP
            ch.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer("发送第一个UDP", CharsetUtil.UTF_8),
                            new InetSocketAddress("127.0.0.1", 9000))).sync();
            if(!ch.closeFuture().await(15000)){
                System.out.println("查询超时！！！");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }
}

class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext,
                                DatagramPacket datagramPacket) throws Exception {

        String response = datagramPacket.content().toString(CharsetUtil.UTF_8);
        if(response.startsWith("结果：")){
            System.out.println(response);
            channelHandlerContext.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)throws Exception{
        ctx.close();
        cause.printStackTrace();
    }
}
