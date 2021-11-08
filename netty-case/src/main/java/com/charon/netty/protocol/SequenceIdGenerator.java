package com.charon.netty.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: netty
 * @description  自增Id
 * @author: charon
 * @create: 2021-11-08 22:54
 **/
public class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
