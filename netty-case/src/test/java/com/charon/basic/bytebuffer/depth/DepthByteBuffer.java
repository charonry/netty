package com.charon.basic.bytebuffer.depth;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-10-05 13:31
 **/
public class DepthByteBuffer {
    public static void main(String[] args) {
        // 默认容量是256，可扩展；区别于NIO自定义容量不可扩展
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        /*// 堆内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
        // 直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(10);*/
        /*System.out.println(buffer.getClass());
        log(buffer);
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < 32 ; i++){
            sb.append("a");
        }
        buffer.writeBytes(sb.toString().getBytes());
        log(buffer);*/

        /*写入后数据大小未超过512，则选择下一个16的整数倍,例如写入后大小为12,则扩容后capacity是16;
        写入后数据大小超过512,则选择下一个2^n，例如写入后大小为 513，则扩容后capacity是2^10=1024（2^9=512 已经不够了）*/
        buffer.writeBytes(new byte[]{1,2,3,4});
        log(buffer);
        buffer.writeInt(5);
        log(buffer);
        buffer.writeInt(6);
        log(buffer);
        System.out.println("-----------------------开始读取------------------");
        for(int i = 0 ; i < 4 ; i++){
            System.out.println(buffer.readByte());
        }
        log(buffer);
        buffer.markReaderIndex();
        System.out.println(buffer.readInt());
        log(buffer);
        // 重复读取:重置到标记位置 reset
        buffer.resetReaderIndex();
        log(buffer);
    }

    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
