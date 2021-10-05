package com.charon.netty.depth;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-10-05 16:38
 **/
@Slf4j
public class HelloWorldServer {

    public static void main(String[] args) {
        new HelloWorldServer().start();
    }

    void start(){
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 半包设置
            serverBootstrap.option(ChannelOption.SO_RCVBUF,10);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            /*socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {

                                }
                            });*/
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080);
            //log.debug("{} binding...", channelFuture.channel())
            channelFuture.sync();
            //log.debug("{} bound...", channelFuture.channel());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server  error" ,e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
