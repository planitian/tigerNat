package com.plani.work.common;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.SocketAddress;


public class ServerHelper {

    public static Channel bind(SocketAddress socketAddress, ChannelInitializer channelInitializer)  {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.INFO));
//        b.option(ChannelOption.SO_KEEPALIVE, false);
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.childHandler(channelInitializer);
  /*      Channel ch = null;
        try {
            ch = b.bind(socketAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Channel ch = null;
        ch = b.bind(socketAddress).channel();
        if (ch != null) {
            ch.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            });
        }else {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        return ch;
    }
}
