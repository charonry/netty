package com.charon.netty.optimize;

import com.charon.netty.protocol.Serializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @program: netty
 * @description Gson转换class类型问题
 * @author: charon
 * @create: 2021-11-08 21:59
 **/
public class TestGson {
    public static void main(String[] args) {
        //System.out.println(new Gson().toJson(String.class));

        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }
}
