package com.charon.netty.message;

import lombok.Data;
import lombok.ToString;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-06 19:55
 **/
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message{

    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
