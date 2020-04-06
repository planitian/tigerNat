package com.plani.work.common.carrier;

public enum PlaniType {
    /**
     * 心跳包
     */
    HEART_BEAT(1),

    /**
     * 代理，客户端 发送给服务端，提示服务端 监听端口，开始代理
     */
    PROXY(2),

    PROXY_SUCCESS(8),
    PROXY_FAILURE(9),
    PROXY_CLIENTID(10),

    ACTIVE(3),

    INACTIVE(4),

    REGISTER(5),

    UNREGISTER(6),
    DATA(7);

    private int type;

    PlaniType(int type) {
        this.type = type;
    }


}
