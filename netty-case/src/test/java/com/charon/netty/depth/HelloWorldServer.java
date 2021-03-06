package com.charon.netty.depth;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
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
            //serverBootstrap.option(ChannelOption.SO_RCVBUF,10);
            // 调整netty的接收缓存区（bytebuf）
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16));
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 固定长度在打印之前
                            //socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            //socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(1);
                            byteBuf.writeBytes(new byte[]{','});
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,byteBuf));
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
