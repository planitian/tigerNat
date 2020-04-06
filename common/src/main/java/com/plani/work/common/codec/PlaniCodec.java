package com.plani.work.common.codec;

import com.plani.work.common.carrier.PlaniMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.io.*;
import java.util.List;

public class PlaniCodec extends MessageToMessageCodec<ByteBuf, PlaniMessage> {


    @Override
    protected void encode(ChannelHandlerContext ctx, PlaniMessage msg, List<Object> out) throws Exception {
        //分配一个  ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();
        byte[] bytes = serializeObject(msg);
        buffer.writeBytes(bytes);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] bytes = ByteBufUtil.getBytes(msg);
        PlaniMessage planiMessage = deSerialize(bytes, PlaniMessage.class);
        out.add(planiMessage);
    }

    public static byte[] serializeObject(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return bytes;
    }

    public static   <T> T deSerialize(byte[] bytes,Class<T> tClass) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return (T) object;
    }
}
