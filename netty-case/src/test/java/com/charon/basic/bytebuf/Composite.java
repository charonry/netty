package com.charon.basic.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.charon.basic.bytebuf.DepthByteBuf.log;

/**
 * @program: netty
 * @description Slic对于bytebuf的整合
 * @author: charon
 * @create: 2021-10-05 15:48
 **/
public class Composite {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // 创建新byteBuf追加所需byteBuf
        /*ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(buf1).writeBytes(buf2);
        log(byteBuf);*/

        CompositeByteBuf byteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        // 不会自动调整写入位置，需要设置第一个参数为true
        buf1.writeByte('x');
        byteBuf.addComponents(true,buf1,buf2);
        // 并不会追加后续添加的元素
        buf1.writeByte('x');
        log(byteBuf);
    }
}
