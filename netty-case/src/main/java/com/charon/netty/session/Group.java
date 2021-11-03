package com.charon.netty.session;

import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * @program: netty
 * @description 聊天组，即聊天室
 * @author: charon
 * @create: 2021-11-03 22:47
 **/
@Data
public class Group {
    // 聊天室名称
    private String name;
    // 聊天室成员
    private Set<String> members;

    public static final Group EMPTY_GROUP = new Group("empty", Collections.emptySet());

    public Group(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }
}
