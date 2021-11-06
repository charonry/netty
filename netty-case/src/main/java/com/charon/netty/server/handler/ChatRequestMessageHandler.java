package com.charon.netty.server.handler;

import com.charon.netty.message.ChatRequestMessage;
import com.charon.netty.message.ChatResponseMessage;
import com.charon.netty.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty
 * @description 聊天处理器
 * @author: charon
 * @create: 2021-11-06 14:00
 **/
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage chat) throws Exception {
        String to = chat.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if(channel != null){
            channel.writeAndFlush(new ChatResponseMessage(chat.getFrom(),chat.getContent()));
            // 这边采用ctx那么是向发送方channel写数据
            //ctx.writeAndFlush(new ChatResponseMessage(chat.getFrom(),chat.getContent()));
        }else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方用户不存在或者不在线"));
        }
    }
}
