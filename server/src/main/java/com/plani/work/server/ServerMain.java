package com.plani.work.server;

import com.plani.work.common.ServerHelper;
import com.plani.work.server.pipe.PlaniServerInitializer;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ServerMain {
//    private static Logger logger = LoggerFactory.getLogger(ServerMain.class);

    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws InterruptedException {
        SslContext sslCtx;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate("plani.com");
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (Exception e) {
            e.printStackTrace();
            sslCtx = null;
        }
        SocketAddress socketAddress = new InetSocketAddress(7741);
        Channel channel = ServerHelper.bind(socketAddress, new PlaniServerInitializer(sslCtx));
        if (channel != null) {
            System.out.println("channel = " + channel);
            System.out.println("server id 成功");
        }
    }

}
