package com.plani.work.server.bind;

import com.plani.work.common.ParentInboundHandler;
import com.plani.work.common.carrier.PlaniMessage;
import com.plani.work.common.carrier.PlaniType;
import com.plani.work.server.PlaniApplication;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;

public class BindServerInboundHandler extends ParentInboundHandler<Object> {

    private ParentInboundHandler parentInboundHandler;

    public BindServerInboundHandler(ParentInboundHandler parentInboundHandler) {
        this.parentInboundHandler = parentInboundHandler;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive  "+this.hashCode());
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered  "+this.hashCode());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered "+this.hashCode());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelWritabilityChanged "+ctx.channel().isWritable()+"   "+this.hashCode());
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded "+this.hashCode());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved  "+this.hashCode());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("bind  收到消息"+this.hashCode());
        System.out.println("bind children id"+ctx.channel().id());
//        byte[] bytes = ByteBufUtil.getBytes(msg);
        PlaniApplication.setBindChannel(ctx.channel());
        byte[] bytes = (byte[]) msg;
        String content = new String(bytes);
        System.out.println(content);
        PlaniMessage planiMessage = new PlaniMessage();
        planiMessage.setPlaniType(PlaniType.DATA);
        planiMessage.setData(bytes);
        planiMessage.setChannelId(ctx.channel().parent().id());
        //用ChannelHandlerContext 是最快的io
        parentInboundHandler.getCtx().writeAndFlush(planiMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("channelInactive  "+this.hashCode());
    }
}
