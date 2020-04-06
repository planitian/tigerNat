package com.plani.work.server.pipe;

import com.plani.work.common.ParentInboundHandler;
import com.plani.work.common.ServerHelper;
import com.plani.work.common.carrier.PlaniMessage;
import com.plani.work.common.carrier.PlaniType;
import com.plani.work.common.component.PlaniChannelGroup;
import com.plani.work.server.PlaniApplication;
import com.plani.work.server.bind.BindServerInboundHandler;
import com.plani.work.server.bind.BindServerInnitializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelMatcher;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端 逻辑
 */
public class PlaniServerHandler extends ParentInboundHandler<PlaniMessage> {


    /**
     * 管理 本地代理连接 和服务器的代理 端口 channelId 映射
     * A closed Channel is automatically removed from the collection
     */
    private PlaniChannelGroup planiChannelGroup = PlaniApplication.getPlaniChannelGroup();



    /**
     * 是否 登录 注册
     */
    private boolean register;

    public boolean isRegister() {
        return register;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("channelRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.ACTIVE);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code", "hello");
        planiMessage.setMeta(objectMap);
        ctx.writeAndFlush(planiMessage);
        System.out.println("channelActive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PlaniMessage msg) throws Exception {
        System.out.println("server read "+msg);
        switch (msg.getPlaniType()) {
            case REGISTER:
                //处理 client 发送的 账号 密码等
                boolean register = dealRegister(msg);
                if (!register) {
                    ctx.channel().close();
                }else {
                    ctx.writeAndFlush(msg);
                }
                break;
            case ACTIVE:
                System.out.println("客户端  端口 ACTIVE ");
                break;
            case PROXY:
                dealProxy(msg);
                break;
            case DATA:
                 dealData(msg);
                 break;
            case INACTIVE:
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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (isClose) {
            planiChannelGroup.close();
        } else {
            ServerHelper.bind(ctx.channel().remoteAddress(), new PlaniServerInitializer(PlaniApplication.getSslCtx()));
        }
        ctx.close();
    }

    private boolean dealRegister(PlaniMessage planiMessage) {
        if (PlaniType.REGISTER == planiMessage.getPlaniType()) {
            Map<String, Object> meta = planiMessage.getMeta();
            String client = (String) meta.get("client");
            String password = (String) meta.get("password");
            //处理 登录
            return true;
        }
        return false;
    }

    private boolean dealProxy(PlaniMessage planiMessage) {
        if (PlaniType.PROXY == planiMessage.getPlaniType()) {
            Map<String, Object> meta = planiMessage.getMeta();
            int remotePort = (int) meta.get("remotePort");
            int localPort = (int) meta.get("localPort");
            ChannelId clientChannelId = (ChannelId) meta.get("clientChannelId");
            SocketAddress socketAddress = new InetSocketAddress(remotePort);
            Channel bind = ServerHelper.bind(socketAddress, new BindServerInnitializer(this));
            System.out.println("bind id"+bind.id());
            PlaniMessage result = new PlaniMessage();
            if (bind != null) {
                planiChannelGroup.addExtra(clientChannelId, bind);
                result.setPlaniType(PlaniType.PROXY_SUCCESS);
                meta.put("serverChannelId", bind.id());
                result.setMeta(meta);
                ctx.writeAndFlush(result);
                return true;
            }else {
                result.setPlaniType(PlaniType.PROXY_FAILURE);
                result.setMeta(meta);
                ctx.writeAndFlush(result);
                return false;
            }
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
            System.out.println("bind  id() = " + channel.id());
            //bind 的 父channel  写入 是没用的， connect的 父channel 才可以写入

     /*       BindServerInboundHandler bindServerInboundHandler = channel.pipeline().get(BindServerInboundHandler.class);
            String content = new String(planiMessage.getData());
            System.out.println(content);
            bindServerInboundHandler.getCtx().writeAndFlush(planiMessage.getData());*/
            PlaniApplication.getBindChannel().writeAndFlush(planiMessage.getData());
            System.out.println("写入");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
