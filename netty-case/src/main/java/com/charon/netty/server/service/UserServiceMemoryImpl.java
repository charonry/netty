package com.charon.netty.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-11-03 22:35
 **/
public class UserServiceMemoryImpl implements UserService{

    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("admin", "123");
        allUserMap.put("charon", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
