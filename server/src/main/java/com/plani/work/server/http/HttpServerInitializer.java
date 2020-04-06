package com.plani.work.server.http;

import com.plani.work.common.ParentInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServerInitializer extends ParentInitializer<SocketChannel> {


    public HttpServerInitializer(SslContext sslCtx) {
        super(sslCtx);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //添加 解码编码器 http的
        pipeline.addLast(new HttpServerCodec());
        //添加 合并成完整 http请求的
        //将http消息的多个部分聚合起来形成一个FullHttpRequest或者FullHttpResponse消息
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

        pipeline.addLast("ss", new StringEncoder());
        //添加处理异常的
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new HttpServerHandler());
    }
}
