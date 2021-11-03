package com.charon.netty.server.service;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 22:56
 **/
public class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
