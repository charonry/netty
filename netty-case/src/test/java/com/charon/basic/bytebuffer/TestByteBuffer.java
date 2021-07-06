package com.charon.basic.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: netty
 * @description ByteBuffer读写文件
 * @author: charon
 * @create: 2021-06-24 20:53
 **/
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        //  1.输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("netty-case\\src\\main\\resources\\data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            do {
                // 从channel读取数据 向buffer写入
                int length = channel.read(buffer);
                log.debug("读到字节数：{}", length);
                if(length == -1){
                    break;
                }
                // 切换buffer的读模式
                buffer.flip();
                while (buffer.hasRemaining()){
                    log.debug("读取到的数据为{}",(char)buffer.get());
                }
                // 切换到写模式
                buffer.clear();
            }while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
