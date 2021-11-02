package com.charon.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * @program: netty
 * @description redis存储内部伪代码
 * @author: charon
 * @create: 2021-11-02 20:29
 **/
public class RedisTest {
    public static void main(String[] args) {
        // 回车换行的字符数值
        final byte[] LINE = {13,10};
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(nioEventLoopGroup);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new LoggingHandler());
                socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeBytes("*3".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$3".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("set".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$4".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("name".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("$6".getBytes());
                        buf.writeBytes(LINE);
                        buf.writeBytes("charon".getBytes());
                        buf.writeBytes(LINE);
                        ctx.writeAndFlush(buf);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        System.out.println(buf.toString(Charset.defaultCharset()));
                    }
                });
            }
        });
    }
}
