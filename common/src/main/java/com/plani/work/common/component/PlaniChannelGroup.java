package com.plani.work.common.component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaniChannelGroup extends AbstractSet<Channel> implements ChannelGroup {

    private Map<ChannelId, Channel> channelMap = new ConcurrentHashMap<>();


    private DefaultChannelGroup defaultChannelGroup;

    private PlaniChannelGroup() {

    }

    public PlaniChannelGroup(DefaultChannelGroup defaultChannelGroup) {
        this.defaultChannelGroup = defaultChannelGroup;
    }

    @Override
    public boolean add(Channel channel) {
        throw new IllegalArgumentException("this method not support ");
    }

    @Override
    public boolean addAll(Collection<? extends Channel> c) {
        throw new IllegalArgumentException("this method not support ");
    }


    @Override
    public boolean remove(Object o) {

        Object remove = remove0(o);
        if (remove == null) {
            return false;
        }
        return defaultChannelGroup.remove(remove);
    }

    private Object remove0(Object o) {
        Iterator<Map.Entry<ChannelId, Channel>> iterator = channelMap.entrySet().iterator();
        Object o1 = null;
        if (o instanceof ChannelId) {
            //如果 传入的是  serverChannelID
            o1 = channelMap.remove(o);
            if (o1 == null) {//传入的是 clientChannelID
                while (iterator.hasNext()) {
                    Map.Entry<ChannelId, Channel> entry = iterator.next();
                    if (entry.getValue().id().equals(o)) {
                        o1 = channelMap.remove(entry.getKey());
                    }
                }
            }
        } else if (o instanceof Channel) {//channel  只会是client的
            ChannelId channelId = null;
            while (iterator.hasNext()) {
                Map.Entry<ChannelId, Channel> entry = iterator.next();
                if (entry.getValue() .equals(o) ) {
                    channelId = entry.getKey();
                    break;
                }
            }
            o1=  channelMap.remove(channelId);
        }
        return o1;
    }

    /**
     *
     * @param serverChannelId 服务端的 channelId ,
     * @param channel 客户端的 channel
     * @return
     */
    public boolean addExtra(ChannelId serverChannelId, Channel channel) {
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("closeFuture "+future.channel().id());
                remove0(future.channel());
            }
        });
        channelMap.put(serverChannelId, channel);
        return defaultChannelGroup.add(channel);
    }

    /**
     * 找到 server端channelId 对于的本地 channel
     * @param channelId server 端的 channelId
     * @return
     */
    public Channel findExtraChannel(ChannelId channelId) {
       return channelMap.get(channelId);
    }
    @Override
    public String name() {
        return defaultChannelGroup.name();
    }

    @Override
    public Channel find(ChannelId id) {
        return defaultChannelGroup.find(id);
    }

    @Override
    public ChannelGroupFuture write(Object message) {
        return defaultChannelGroup.write(message);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
        return defaultChannelGroup.write(message, matcher);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise) {
        return defaultChannelGroup.write(message, matcher, voidPromise);
    }

    @Override
    public ChannelGroup flush() {
        return defaultChannelGroup.flush();
    }

    @Override
    public ChannelGroup flush(ChannelMatcher matcher) {
        return defaultChannelGroup.flush(matcher);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message) {
        return defaultChannelGroup.writeAndFlush(message);
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message) {
        return defaultChannelGroup.flushAndWrite(message);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
        return defaultChannelGroup.writeAndFlush(message, matcher);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
        return defaultChannelGroup.writeAndFlush(message, matcher, voidPromise);
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
        return defaultChannelGroup.flushAndWrite(message, matcher);
    }

    @Override
    public ChannelGroupFuture disconnect() {
        return defaultChannelGroup.disconnect();
    }

    @Override
    public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
        return defaultChannelGroup.disconnect(matcher);
    }

    @Override
    public ChannelGroupFuture close() {
        return defaultChannelGroup.close();
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher matcher) {
        return defaultChannelGroup.close(matcher);
    }

    @Override
    public ChannelGroupFuture deregister() {
        return defaultChannelGroup.deregister();
    }

    @Override
    public ChannelGroupFuture deregister(ChannelMatcher matcher) {
        return defaultChannelGroup.deregister(matcher);
    }

    @Override
    public ChannelGroupFuture newCloseFuture() {
        return defaultChannelGroup.newCloseFuture();
    }

    @Override
    public ChannelGroupFuture newCloseFuture(ChannelMatcher matcher) {
        return defaultChannelGroup.newCloseFuture(matcher);
    }

    @Override
    public int compareTo(ChannelGroup o) {
        return defaultChannelGroup.compareTo(o);
    }

    @Override
    public Iterator<Channel> iterator() {
        return defaultChannelGroup.iterator();
    }

    @Override
    public int size() {
        return defaultChannelGroup.size();
    }
}
