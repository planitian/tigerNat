package com.plani.work.common;

import com.plani.work.common.carrier.PlaniMessage;
import com.plani.work.common.carrier.PlaniType;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class ParentInboundHandler<T> extends SimpleChannelInboundHandler<T> {
//    private Logger logger = LoggerFactory.getLogger(ParentInboundHandler.class);

    protected ChannelHandlerContext ctx;


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    /**
     * 是否 手动关闭
     */
    protected boolean isClose = false;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ParentInboundHandler.this.isClose = true;
            }
        });
        this.ctx = ctx;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        logger.error("handler", cause);
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
//                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
//                ctx.close();
            } else if (e.state() == IdleState.ALL_IDLE) {
                PlaniMessage planiMessage = new PlaniMessage();
                planiMessage.setPlaniType(PlaniType.HEART_BEAT);
                ctx.writeAndFlush(planiMessage);
            }
        }
    }
}
