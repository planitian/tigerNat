package com.plani.work.server.pipe;

import com.plani.work.common.ParentInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * 用于 自定义的plani 协议的ChannelInitializer
 *
 */
public class PlaniServerInitializer extends ParentInitializer<SocketChannel> {

    public PlaniServerInitializer(SslContext sslCtx) {
        super(sslCtx);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new PlaniServerHandler());
    }
}
