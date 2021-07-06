package com.charon.basic.bytebuffer;

import com.charon.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @program: netty
 * @description ByteBuffer的读写原理
 * @author: charon
 * @create: 2021-06-24 22:52
 **/
@Slf4j
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64});
        ByteBufferUtil.debugAll(buffer);
        buffer.flip();
        log.debug("buffer中读数据为{}:",buffer.get());
        ByteBufferUtil.debugAll(buffer);
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x6f});
        ByteBufferUtil.debugAll(buffer);
    }
}
