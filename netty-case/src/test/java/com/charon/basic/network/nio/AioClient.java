package com.charon.basic.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-07-19 22:53
 **/
public class AioClient {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // 创建 Client
        AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
        // 与 Server 连接
        sc.connect(new InetSocketAddress("localhost", 8080)).get();
        // 向 Server 写数据
        sc.write(Charset.defaultCharset().encode("hello"));
        System.in.read();
    }
}
