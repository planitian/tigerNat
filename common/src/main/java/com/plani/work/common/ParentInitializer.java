package com.plani.work.common;

import com.plani.work.common.codec.PlaniCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 通用 父类  ChannelInitializer
 *
 * @param <T>
 */
public class ParentInitializer<T extends Channel> extends ChannelInitializer<T> {

    private final SslContext sslCtx;

    public ParentInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(T ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        // 异步处理 大数据流
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new PlaniCodec());
    }
}
