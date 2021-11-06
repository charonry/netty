package com.charon.netty.server.handler;

import com.charon.netty.message.GroupChatRequestMessage;
import com.charon.netty.message.GroupChatResponseMessage;
import com.charon.netty.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-06 14:42
 **/
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> membersChannels = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());
        for(Channel channel:membersChannels){
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
