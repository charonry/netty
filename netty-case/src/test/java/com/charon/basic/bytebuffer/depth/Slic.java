package com.charon.basic.bytebuffer.depth;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.charon.basic.bytebuffer.depth.DepthByteBuffer.log;

/**
 * @program: netty
 * @description Slic对于bytebuf逻辑层的分割
 * @author: charon
 * @create: 2021-10-05 15:21
 **/
public class Slic {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buffer);
        // 在切片过程中，没有发生数据复制
        ByteBuf bf1 = buffer.slice(0, 5);
        ByteBuf bf2 = buffer.slice(5, 5);
        // 与原始ByteBuf使用同一块底层内存.截取全部
        //buffer.duplicate();
        // 切片后固定区间大小，无法追加write；报IndexOutOfBoundsException异常
        //bf1.writeByte('x');
        log(bf1);
        log(bf2);
        // 切割后内存和原始共享一块内存，原内存释放那么切割后内存也无了；除非先retain后release
        /*System.out.println("---------------------------释放原始内存---------------------------");
        buffer.release();
        log(bf1);*/
        System.out.println("---------------------------修改内容---------------------------");
        bf1.setByte(0,'c');
        log(buffer);
    }
}
