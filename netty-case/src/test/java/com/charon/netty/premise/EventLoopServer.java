package com.charon.netty.premise;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-09-14 14:53
 **/
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        // 细分2：创建一个独立的 EventLoopGroup
        EventLoopGroup group = new DefaultEventLoop();
        new ServerBootstrap()
                // boss:只负责accept事件;woker:只负责socketChannel上的读写
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("handler",new ChannelInboundHandlerAdapter(){
                            @Override
                            // 此时数据类型为ByteBuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(Charset.defaultCharset()));
                                // 让消息传递给下一个handler
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(group,"nextHandler",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.debug(byteBuf.toString(Charset.defaultCharset()));
                                // 建议使用 ctx.alloc() 创建 ByteBuf
                                ByteBuf response = ctx.alloc().buffer();
                                response.writeBytes(byteBuf);
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
