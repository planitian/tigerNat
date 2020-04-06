package com.plani.work.common.high;

import com.plani.work.common.ParentInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class HighInitializer extends ParentInitializer<SocketChannel> {
    public HighInitializer(SslContext sslCtx) {
        super(sslCtx);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast()
    }
}
