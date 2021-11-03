package com.charon.netty.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 22:43
 **/
public class SessionMemoryImpl implements Session {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    private final Map<Channel,Map<String,Object>> channelAttributesMap = new ConcurrentHashMap<>();


    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
        channelAttributesMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributesMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}
