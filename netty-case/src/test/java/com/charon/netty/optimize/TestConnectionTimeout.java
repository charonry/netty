package com.charon.netty.optimize;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty
 * @description 连接超时
 * @author: charon
 * @create: 2021-11-06 17:47
 **/
@Slf4j
public class TestConnectionTimeout {
    public static void main(String[] args) {
        // 1. 客户端通过 .option() 方法配置参数 给 SocketChannel 配置参数
        // 2. 服务器端
        //new ServerBootstrap().option(); // 是给 ServerSocketChannel 配置参数
        //new ServerBootstrap().childOption(); // 给 SocketChannel 配置参数
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    // 默认2秒，如果超过23秒就算设置太多时间也不会去等待客户端响应
                    // 超过2秒:java.net.ConnectException: Connection refused: no further information
                    // 低于2秒:io.netty.channel.ConnectTimeoutException: connection timed out:
                    // CONNECT_TIMEOUT_MILLIS:用在客户端建立连接时
                    // SO_TIMEOUT:主要用在阻塞 IO
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);
            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("timeout");
        } finally {
            group.shutdownGracefully();
        }
    }
}
