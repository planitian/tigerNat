package com.plani.work.client;

import com.plani.work.client.pipe.PlaniClientInboundHandler;
import com.plani.work.client.pipe.PlaniClientInitializer;
import com.plani.work.common.ClientHelper;
import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ClientMain {



    public static void main(String[] args) throws InterruptedException {
        SslContext sslCtx;
        try {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (Exception e) {
            e.printStackTrace();
            sslCtx = null;
        }
        PlaniApplication.setSslCtx(sslCtx);
        SocketAddress socketAddress = new InetSocketAddress("localhost", 7741);
        Channel channel = ClientHelper.connect(socketAddress, new PlaniClientInitializer(PlaniApplication.getSslCtx()));
        TimeUnit.SECONDS.sleep(2);
        if (channel != null) {
            PlaniClientInboundHandler clientHandler = channel.pipeline().get(PlaniClientInboundHandler.class);
            if (clientHandler.isRegister()) {
                boolean progressProxy = clientHandler.progressProxy(9999, 8888);
                System.out.println("progressProxy = " + progressProxy);
            }
        }
    }
}
