package com.charon.netty.protocol;

import com.charon.netty.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
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
                // 防止粘包和半包的出现
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new MessageCodec());

        // encode
        LoginRequestMessage message = new LoginRequestMessage("charon", "123");
        embeddedChannel.writeOutbound(message);
        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        //embeddedChannel.writeInbound(buf);

        // 复现出现粘包半包的现象
        ByteBuf s1 = buf.slice(0, 100);
        ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
        s1.retain(); // 引用计数 2
        embeddedChannel.writeInbound(s1); // release 1
        embeddedChannel.writeInbound(s2);
    }
}
