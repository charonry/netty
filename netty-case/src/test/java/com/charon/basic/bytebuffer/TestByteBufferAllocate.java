package com.charon.basic.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @program: netty
 * @description tByteBuffer的分配空间
 * @author: charon
 * @create: 2021-06-24 23:13
 **/
@Slf4j
public class TestByteBufferAllocate {

    public static void main(String[] args) {
        log.debug("allocate的类为{}",ByteBuffer.allocate(16).getClass());
        log.debug("allocate的类为{}",ByteBuffer.allocateDirect(16).getClass());
        /*
        class java.nio.HeapByteBuffer    - java 堆内存，读写效率较低，受到 GC 的影响
        class java.nio.DirectByteBuffer  - 直接内存，读写效率高（少一次拷贝），不会受 GC 影响，分配的效率低
         */
    }
}
