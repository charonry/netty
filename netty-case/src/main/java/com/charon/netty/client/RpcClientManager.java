package com.charon.netty.client;

import com.charon.netty.client.handler.RpcResponseMessageHandler;
import com.charon.netty.message.RpcRequestMessage;
import com.charon.netty.protocol.MessageCodecSharable;
import com.charon.netty.protocol.ProcotolFrameDecoder;
import com.charon.netty.protocol.SequenceIdGenerator;
import com.charon.netty.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-06 20:02
 **/
@Slf4j
public class RpcClientManager {
    static volatile Channel channel = null;
    static Object lock = new Object();

    public static void main(String[] args) {
        /*Channel channel = SingleChannel.INSTANCE.getEnum().getSingleChannel();
        channel.writeAndFlush(new RpcRequestMessage(1,
                "com.charon.netty.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"chairn"}));*/
        HelloService service = getProxyService(HelloService.class);
        service.sayHello("charon");
        service.sayHello("你好");
    }

    /**
     * 创建代理类
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 将消息对象发送出去
            getChannel().writeAndFlush(msg);
            return null;
        });
        return (T) o;
    }

    /**
     * 单例获取channel
     * @return
     */
    public static  Channel getChannel(){
        if(channel != null){
            return channel;
        }
        synchronized (lock){
            if(channel != null){
                return channel;
            }
            initchannel();
            return channel;
        }
    }
     public enum SingleChannel{
        INSTANCE;

        SingleChannel() {
        }

        public Channel getSingleChannel(){
            initchannel();
            return channel;
        }

        public SingleChannel getEnum(){
            return  INSTANCE ;
        }
    }

    /**
     * 初始化channel
     */
    private static void initchannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("client error", e);
        }

    }
}
