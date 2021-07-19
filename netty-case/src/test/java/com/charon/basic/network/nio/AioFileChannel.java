package com.charon.basic.network.nio;

import com.charon.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-07-19 22:24
 **/
@Slf4j
public class AioFileChannel {
    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.
                open(Paths.get("netty-case\\src\\main\\resources\\data.txt"), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin...");
            /**
             * 参数1 ByteBuffer
             * 参数2 读取的起始位置
             * 参数3 附件
             * 参数4 回调对象 CompletionHandler
             */
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override // read 成功
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}", result);
                    attachment.flip();
                    ByteBufferUtil.debugAll(attachment);
                }

                @Override // read 失败
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 防止守护线程随着主线程结束而结束
        System.in.read();
    }
}
