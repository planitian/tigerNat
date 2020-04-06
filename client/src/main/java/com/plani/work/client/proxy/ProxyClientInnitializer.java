package com.plani.work.client.proxy;

import com.plani.work.common.ParentInboundHandler;
import com.plani.work.common.ParentInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ProxyClientInnitializer extends ChannelInitializer<SocketChannel> {


    private ParentInboundHandler parentInboundHandler;

    public ProxyClientInnitializer(ParentInboundHandler parentInboundHandler) {
        this.parentInboundHandler = parentInboundHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 异步处理 大数据流
//        pipeline.addLast(new ChunkedWriteHandler());

//        pipeline.addLast(new ByteArrayDecoder());
//        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new ProxyClientInboundHandler(parentInboundHandler));

    }
}
