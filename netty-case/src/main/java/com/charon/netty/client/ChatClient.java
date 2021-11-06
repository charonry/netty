package com.charon.netty.client;

import com.charon.netty.message.LoginRequestMessage;
import com.charon.netty.message.LoginResponseMessage;
import com.charon.netty.protocol.MessageCodecSharable;
import com.charon.netty.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 23:08
 **/
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN_LATCH = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProcotolFrameDecoder());
                    //socketChannel.pipeline().addLast(LOGGING_HANDLER);
                    socketChannel.pipeline().addLast(MESSAGE_CODEC);
                    socketChannel.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){
                        // 接受消息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("返回msg:{}",msg);
                            if(msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if(response.isSuccess()){
                                    LOGIN.set(true);
                                }
                                WAIT_FOR_LOGIN_LATCH.countDown();
                            }
                        }

                        // 连接建立后触发active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责用户的连接建立
                            new Thread(()->{
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                // 发送消息
                                ctx.writeAndFlush(message);

                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN_LATCH.await();
                                } catch (InterruptedException e) {
                                    log.error("等待登录异常信息{}",e);
                                }
                                // 登录状态
                                if(!LOGIN.get()){
                                    ctx.channel().close();
                                    return ;
                                }
                                while (true){
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                }
                            },"system in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error",e);
        }finally {
            group.shutdownGracefully();
        }
    }

}
