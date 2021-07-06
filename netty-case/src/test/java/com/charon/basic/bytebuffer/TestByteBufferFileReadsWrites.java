package com.charon.basic.bytebuffer;

import com.charon.util.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @program: netty
 * @description 文件拆解合并读写
 * @author: charon
 * @create: 2021-06-29 22:37
 **/
public class TestByteBufferFileReadsWrites {
    public static void main(String[] args) {
        //bufferReads();
        bufferWrites();

    }

    /**
     * 多文件写入
     */
    private static void bufferWrites() {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        try (FileChannel channel = new RandomAccessFile("netty-case\\src\\main\\resources\\world.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
    }

    /**
     * 多文件读取
     */
    private static void bufferReads() {
        try (FileChannel channel = new RandomAccessFile("netty-case\\src\\main\\resources\\data.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(6);
            ByteBuffer b2 = ByteBuffer.allocate(6);
            ByteBuffer b3 = ByteBuffer.allocate(6);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);
            ByteBufferUtil.debugAll(b3);
        } catch (IOException e) {
        }
    }
}
