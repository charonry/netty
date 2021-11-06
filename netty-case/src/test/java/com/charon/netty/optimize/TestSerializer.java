package com.charon.netty.optimize;


import com.charon.netty.config.Config;
import com.charon.netty.message.LoginRequestMessage;
import com.charon.netty.message.Message;
import com.charon.netty.protocol.MessageCodecSharable;
import com.charon.netty.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-06 17:12
 **/
public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);
        LoginRequestMessage message = new LoginRequestMessage("charon", "2323");
        //channel.writeOutbound(message);
        ByteBuf buf = messageToByteBuf(message);
        channel.writeInbound(buf);
    }

    public static ByteBuf messageToByteBuf(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{'c','h','o','n'});
        buf.writeByte(1);
        buf.writeByte(algorithm);
        buf.writeByte(msg.getMessageType());
        buf.writeInt(msg.getSequenceId());
        buf.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }
}
