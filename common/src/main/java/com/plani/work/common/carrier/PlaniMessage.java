package com.plani.work.common.carrier;

import io.netty.channel.ChannelId;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * plani 协议的 载体 bean 序列化的载体
 */
@Data
public class PlaniMessage implements Serializable {

    private static final long serialVersionUID = -5998495740848054575L;
    private PlaniType planiType;

    private Map<String, Object> meta;

    private ChannelId channelId;

    private byte[] data;

}
