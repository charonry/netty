package com.charon.netty.server.handler;

import com.charon.netty.message.RpcRequestMessage;
import com.charon.netty.message.RpcResponseMessage;
import com.charon.netty.server.service.HelloService;
import com.charon.netty.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-06 20:01
 **/
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());
        try {
            // 获取真正的实现对象
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            // 获取要调用的方法
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            // 调用方法
            Object invoke = method.invoke(service, msg.getParameterValue());
            // 调用成功
            response.setReturnValue(invoke);
        }  catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错:" + e.getCause().getMessage()));
        }
        // 返回结果
        ctx.writeAndFlush(response);
    }

}
