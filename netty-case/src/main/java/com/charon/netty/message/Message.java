package com.charon.netty.message;

import lombok.Data;

import java.io.Serializable;


/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-02 21:44
 **/
@Data
public abstract class Message implements Serializable {

    private static final long serialVersionUID = -1566143744991341930L;

    public abstract int getMessageType();

    // 请求序号
    private int sequenceId;

    // 指令类型
    private int messageType;

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;
    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;
    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;
    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;
    public static final int PingMessage = 14;
    public static final int PongMessage = 15;


}
