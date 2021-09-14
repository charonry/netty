package com.charon.netty;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description 事件循环对象
 * @author: charon
 * @create: 2021-09-14 14:26
 **/
@Slf4j
public class EventLoop {
    public static void main(String[] args) {
        // 1. 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2);// io 事件，普通任务，定时任务
        //EventLoopGroup group = new DefaultEventLoopGroup(); // 普通任务，定时任务
        //System.out.println(NettyRuntime.availableProcessors());

        // 2. 获取下一个事件循环对象
        /*System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());*/

        // 2. 执行普通任务
        /*for(int i = 0;i<4;i++){
            int temp = i ;
            group.next().submit(()->{
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("event-"+temp);
            });
        }
        log.debug("main");*/

        // 4. 执行定时任务
        for(int i = 0;i<4;i++){
            int temp = i;
            group.next().scheduleAtFixedRate(() -> {
                log.debug(temp+"ok");
            }, 1, 4, TimeUnit.SECONDS);
        }
        log.debug("main");
    }
}
