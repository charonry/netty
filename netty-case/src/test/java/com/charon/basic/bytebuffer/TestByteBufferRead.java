package com.charon.basic.bytebuffer;

import com.charon.util.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * @program: netty
 * @description ByteBuffer读写方法
 * @author: charon
 * @create: 2021-06-24 23:24
 **/
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // rewind从头开始读
        /*buffer.get(new byte[4]);
        ByteBufferUtil.debugAll(buffer);
        buffer.rewind();
        System.out.println((char)buffer.get());*/

        // mark & reset
        // mark 做一个标记，记录 position 位置， reset 是将 position 重置到 mark 的位置
        /*System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        // 加标记，索引2 的位置
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        // 将 position 重置到索引 2
        buffer.reset();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());*/

        // get(i) 不会改变读索引的位置
        System.out.println((char) buffer.get(3));
        ByteBufferUtil.debugAll(buffer);
    }
}
