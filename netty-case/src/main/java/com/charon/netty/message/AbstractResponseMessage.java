package com.charon.netty.message;

import lombok.Data;
import lombok.ToString;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-02 22:01
 **/
@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage  extends Message{
    private boolean success;
    private String reason;


    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
