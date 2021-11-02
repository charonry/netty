package com.charon.netty.protocol;

import com.charon.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-02 21:45
 **/
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf buf) throws Exception {
        // 1. 4 字节的魔数
        buf.writeBytes(new byte[]{'c','h','o','n'});
        //buf.writeBytes(new byte[]{'c','h','o','n'});
        // 2. 1 字节的版本
        buf.writeByte(1);
        // 3. 1 字节的序列化方式。JDK:0;Json:1
        buf.writeByte(0);
        // 4. 1 字节的指令类型
        buf.writeByte(msg.getMessageType());
        // 5. 4 字节的请求序号
        buf.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        buf.writeByte(0xff);
        // 6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        buf.writeInt(bytes.length);
        // 8. 写入内容
        buf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        int magicNum = buf.readInt();
        byte version = buf.readByte();
        byte serializerType = buf.readByte();
        byte messageType = buf.readByte();
        int sequenceId = buf.readInt();
        buf.readByte();
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        list.add(message);
    }
}
