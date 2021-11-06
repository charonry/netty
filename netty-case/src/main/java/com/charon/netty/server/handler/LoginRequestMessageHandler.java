package com.charon.netty.server.handler;

import com.charon.netty.message.LoginRequestMessage;
import com.charon.netty.message.LoginResponseMessage;
import com.charon.netty.server.service.UserServiceFactory;
import com.charon.netty.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty
 * @description 登录处理器
 * @author: charon
 * @create: 2021-11-06 11:31
 **/
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if(login){
            SessionFactory.getSession().bind(ctx.channel(),username);
            message = new LoginResponseMessage(true, "登录成功");
        }else {
            message = new LoginResponseMessage(false, "账号密码不正确");
        }
        ctx.writeAndFlush(message);
    }
}
