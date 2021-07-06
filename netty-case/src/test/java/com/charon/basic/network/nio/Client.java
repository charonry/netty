package com.charon.basic.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-07-01 20:59
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        sc.write(Charset.defaultCharset().encode("hello\nwbcorld\n"));
        System.in.read();
    }
}
