package com.charon.netty.server.service;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-08 21:13
 **/
public class HelloServiceImpl implements  HelloService {
    @Override
    public String sayHello(String msg) {
        //int i = 1 / 0;
        return "你好, " + msg;
    }
}
