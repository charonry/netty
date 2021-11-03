package com.charon.netty.session;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 22:56
 **/
public class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public  static GroupSession getGroupSession() {
        return session;
    }

}
