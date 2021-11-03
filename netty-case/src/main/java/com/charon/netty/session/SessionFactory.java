package com.charon.netty.session;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 22:57
 **/
public class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
