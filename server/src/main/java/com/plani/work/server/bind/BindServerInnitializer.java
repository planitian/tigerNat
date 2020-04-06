package com.plani.work.server.bind;

import com.plani.work.common.ParentInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class BindServerInnitializer  extends ChannelInitializer<SocketChannel> {

    private ParentInboundHandler parentInboundHandler;

    public BindServerInnitializer(ParentInboundHandler parentInboundHandler) {
        this.parentInboundHandler = parentInboundHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 异步处理 大数据流
//        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
//        pipeline.addLast(new HttpResponseDecoder());
        pipeline.addLast(new BindServerInboundHandler(parentInboundHandler));
    }
}
