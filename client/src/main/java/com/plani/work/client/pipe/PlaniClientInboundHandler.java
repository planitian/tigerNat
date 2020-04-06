package com.plani.work.client.pipe;

import com.plani.work.client.PlaniApplication;
import com.plani.work.client.proxy.ProxyClientInnitializer;
import com.plani.work.common.ClientHelper;
import com.plani.work.common.ParentInboundHandler;
import com.plani.work.common.carrier.PlaniMessage;
import com.plani.work.common.carrier.PlaniType;
import com.plani.work.common.component.PlaniChannelGroup;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 客户端 逻辑
 */
public class PlaniClientInboundHandler extends ParentInboundHandler<PlaniMessage> {


    /**
     * 管理 本地代理连接 和服务器的代理 端口 channelId 映射
     * A closed Channel is automatically removed from the collection
     */
    private final PlaniChannelGroup planiChannelGroup = PlaniApplication.getPlaniChannelGroup();

    private final ChannelGroup tempChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 是否 登录 注册
     */
    private boolean register;

    public boolean isRegister() {
        return register;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.REGISTER);//客户端 注册
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("client", "plani");
        objectMap.put("password", "123456");
        //客端信息
        planiMessage.setMeta(objectMap);
        ctx.writeAndFlush(planiMessage);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channelInactive ");
        if (isClose) {
            planiChannelGroup.close();
        } else {
            //重新连接
            ClientHelper.connect(ctx.channel().remoteAddress(), new PlaniClientInitializer(PlaniApplication.getSslCtx()));
        }
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PlaniMessage msg) throws Exception {
        System.out.println("client read "+msg);
        switch (msg.getPlaniType()) {
            case REGISTER:
                System.out.println("登录 注册成功");
                register = true;
                break;
            case ACTIVE:
                System.out.println("服务端 端口 ACTIVE ");
                break;
            case PROXY_SUCCESS:
                System.out.println("服务端 端口 代理成功");
                dealProxySuccess(msg);
                break;
            case PROXY_FAILURE:
                System.out.println("代理失败");
                break;
            case DATA:
                dealData(msg);
                break;
            case HEART_BEAT:
                //do  nothing
                break;
            case UNREGISTER:
                //关闭 本地 所有代理
                planiChannelGroup.close();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.UNREGISTER);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("client", "plani");
        objectMap.put("cause", "exceptionCaught");
        objectMap.put("Throwable", cause);
        //客端信息
        planiMessage.setMeta(objectMap);
        ctx.writeAndFlush(planiMessage);
    }


    /**
     * 通知 服务器  代理端口
     *
     * @param remotePort
     * @param localPort
     */
    public boolean progressProxy(int remotePort, int localPort) {
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.PROXY);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("remotePort", remotePort);
        objectMap.put("localPort", localPort);
        SocketAddress socketAddress = new InetSocketAddress(localPort);
        Channel connect = ClientHelper.send(socketAddress, new ProxyClientInnitializer(this));
        if (connect != null) {
            tempChannelGroup.add(connect);
            objectMap.put("clientChannelId", connect.id());
            planiMessage.setMeta(objectMap);
            ctx.writeAndFlush(planiMessage);
            return true;
        }
        return false;
    }

    public void dealProxySuccess(PlaniMessage planiMessage) {
        Map<String, Object> meta = planiMessage.getMeta();
        ChannelId serverChannelId = (ChannelId) meta.get("serverChannelId");
        ChannelId clientChannelId = (ChannelId) meta.get("clientChannelId");
        Channel channel = tempChannelGroup.find(clientChannelId);
//        tempChannelGroup.remove(channel);
        System.out.println("serverChannelId = " + serverChannelId);
        boolean addServerMapLocal = planiChannelGroup.addExtra(serverChannelId, channel);
        System.out.println("addServerMapLocal = " + addServerMapLocal);
    }


    /**
     * 关闭 本地代理的连接
     *
     * @param port 代理的端口
     * @return
     */
    public boolean closeProxy(int port) {
        Iterator<Channel> iterator = planiChannelGroup.iterator();
        Channel channel = null;
        while (iterator.hasNext()) {
            Channel next = iterator.next();
            int tempPort = ((InetSocketAddress) next.localAddress()).getPort();
            if (port == tempPort) {
                channel = next;
                break;
            }
        }
        if (channel != null) {
            channel.close();
            return true;
        }
        return false;
    }

    /**
     * 发送数据
     * @param planiMessage
     */
    private void dealData(PlaniMessage planiMessage) {
        try {
            ChannelId channelId = planiMessage.getChannelId();
     /*   planiChannelGroup.writeAndFlush(planiMessage, new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                return channelId.equals(channel.id());
            }
        });*/
            System.out.println("channelId = " + channelId);
            Channel channel = planiChannelGroup.findExtraChannel(channelId);
            System.out.println("channel.isWritable() = " + channel.isWritable());
            ByteBuf byteBuf = channel.alloc().ioBuffer();
            byteBuf.writeBytes(planiMessage.getData());
            channel.writeAndFlush(byteBuf);
            System.out.println("写入");
        } catch (Exception e) {

        }
    }
}
