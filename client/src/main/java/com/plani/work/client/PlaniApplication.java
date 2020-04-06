package com.plani.work.client;

import com.plani.work.common.component.PlaniChannelGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;


public class PlaniApplication {

    /**
     * 管理 本地代理连接 和服务器的代理 端口 channelId 映射
     * A closed Channel is automatically removed from the collection
     */
    private static PlaniChannelGroup planiChannelGroup = new PlaniChannelGroup(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));

    public static PlaniChannelGroup getPlaniChannelGroup() {
        return planiChannelGroup;
    }

    private static SslContext sslCtx;

    public static SslContext getSslCtx() {
        return sslCtx;
    }

    public static void setSslCtx(SslContext sslCtx) {
        PlaniApplication.sslCtx = sslCtx;
    }
}
