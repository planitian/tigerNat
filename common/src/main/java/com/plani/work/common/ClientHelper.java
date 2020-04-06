package com.plani.work.common;

import com.plani.work.common.carrier.PlaniMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.SocketAddress;
import java.util.Objects;

public class ClientHelper {

    public static Channel connect(SocketAddress socketAddress, ChannelInitializer channelInitializer) {

        Objects.requireNonNull(socketAddress, "host can not null");
        Objects.requireNonNull(channelInitializer, "channelInitializer can not null");
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.INFO));
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(channelInitializer);
        Channel ch = null;
        for (int i = 0; i < 3; i++) {
            try {
                ChannelFuture channelFuture = b.connect(socketAddress).sync();
                ch = channelFuture.channel();
                break;
            } catch (Exception e) {
                System.out.println("重新连接 "+i);
            }
        }
        if (ch != null) {
            ch.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                }
            });
        }else {
            workerGroup.shutdownGracefully();
        }
        return ch;
    }

    public static Channel send(SocketAddress socketAddress,ChannelInitializer channelInitializer ){
        Objects.requireNonNull(socketAddress, "host can not null");
        Objects.requireNonNull(channelInitializer, "channelInitializer can not null");
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.INFO));
//        b.option(ChannelOption.SO_KEEPALIVE, false);
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.handler(channelInitializer);
        Channel ch = null;
        for (int i = 0; i < 3; i++) {
            try {
                ChannelFuture channelFuture = b.connect(socketAddress);
                ch = channelFuture.channel();
                break;
            } catch (Exception e) {
                System.out.println("重新连接 "+i);
            }
        }
        if (ch != null) {
            ch.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                }
            });
        }else {
            workerGroup.shutdownGracefully();
        }
        return ch;
    }
}
