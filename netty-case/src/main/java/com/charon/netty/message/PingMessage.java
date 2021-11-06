package com.charon.netty.message;

/**
 * @program: netty
 * @description 心跳包
 * @author: charon
 * @create: 2021-11-06 16:10
 **/
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
