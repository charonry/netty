package com.charon.netty.depth;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Random;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-10-05 16:48
 **/
@Slf4j
public class HelloWorldClient {
    public static void main(String[] args) {
        // 初始发送数据
        send();
        System.out.println("finish");
    }

    public static byte[] fill10Bytes(char c, int len){
        byte[] bytes = new byte[10];
        Arrays.fill(bytes, (byte) '_');
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) c;
        }
        System.out.println(new String(bytes));
        return bytes;
    }

    public static StringBuilder makeString(char c, int len) {
        StringBuilder sb = new StringBuilder(len + 1);
        for (int i = 0; i < len; i++) {
            sb.append(c);
        }
        sb.append(",");
        return sb;
    }

    public static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(worker)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                // 会在连接建立成功之后出发activities事件
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    char c = '0';
                                    Random r = new Random();
                                    for (int i = 0; i < 10; i++) {
                                        //byte[] bytes = fill10Bytes(c, r.nextInt(10) + 1);
                                        StringBuilder sb = makeString(c, r.nextInt(256)+1);
                                        c++;
                                        buf.writeBytes(sb.toString().getBytes());
                                       /* ByteBuf buffer = ctx.alloc().buffer();
                                        buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,16,17});
                                        ctx.writeAndFlush(buffer);
                                        // 发完即关
                                        ctx.channel().close();*/
                                    }
                                    ctx.writeAndFlush(buf);
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8080)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client  error" ,e);
        }finally {
            worker.shutdownGracefully();
        }
    }
}
