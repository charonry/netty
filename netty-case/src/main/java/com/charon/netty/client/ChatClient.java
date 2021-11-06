package com.charon.netty.client;

import com.charon.netty.message.*;
import com.charon.netty.protocol.MessageCodecSharable;
import com.charon.netty.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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

    static CountDownLatch WAIT_FOR_LOGIN_LATCH = new CountDownLatch(1);
    static AtomicBoolean LOGIN = new AtomicBoolean(false);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

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
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    socketChannel.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                //log.debug("3s 没有写数据了，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
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
                            userOperate(ctx);
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经异常断开，按任意键退出..{}", cause.getMessage());
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


    /**
     * 用户的操作
     */
    public static void userOperate(ChannelHandlerContext ctx){
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
                String command = scanner.nextLine();
                String[] s = command.split(" ");
                switch (s[0]){
                    case "send":
                        ctx.writeAndFlush(new ChatRequestMessage(username,s[1],s[2]));
                        break;
                    case "gsend":
                        ctx.writeAndFlush(new GroupChatRequestMessage(username,s[1],s[2]));
                        break;
                    case "gcreate":
                        Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                        set.add(username);
                        ctx.writeAndFlush(new GroupCreateRequestMessage(s[1],set));
                        break;
                    case "gmembers":
                        ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                        break;
                    case "gjoin":
                        ctx.writeAndFlush(new GroupJoinRequestMessage(username,s[1]));
                        break;
                    case "gquit":
                        ctx.writeAndFlush(new GroupQuitRequestMessage(username,s[1]));
                        break;
                    case "quit":
                        ctx.channel().close();
                        return;
                }
            }
        },"system in").start();
    }
}
