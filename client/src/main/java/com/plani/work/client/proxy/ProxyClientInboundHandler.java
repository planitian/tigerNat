package com.plani.work.client.proxy;

import com.plani.work.common.ParentInboundHandler;
import com.plani.work.common.carrier.PlaniMessage;
import com.plani.work.common.carrier.PlaniType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;

public class ProxyClientInboundHandler extends ParentInboundHandler<ByteBuf> {

    private ParentInboundHandler parentInboundHandler;

    public ProxyClientInboundHandler(ParentInboundHandler parentInboundHandler) {
        this.parentInboundHandler = parentInboundHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("proxy 收到信息"+msg);
        byte[] bytes = ByteBufUtil.getBytes(msg);
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.DATA);
        planiMessage.setData(bytes);
        planiMessage.setChannelId(ctx.channel().id());
        //用ChannelHandlerContext 是最快的io
       parentInboundHandler.getCtx().writeAndFlush(planiMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
