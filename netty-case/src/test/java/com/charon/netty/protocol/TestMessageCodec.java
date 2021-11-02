package com.charon.netty.protocol;

import com.charon.netty.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-02 22:37
 **/
public class TestMessageCodec {
    public static void main(String[] args) throws Exception{
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(),
                new MessageCodec());

        // encode
        LoginRequestMessage message = new LoginRequestMessage("charon", "123");
        embeddedChannel.writeOutbound(message);
        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        embeddedChannel.writeInbound(buf);
    }
}
