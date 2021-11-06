package com.charon.netty.premise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-09-16 16:29
 **/
@Slf4j
public class NettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup  group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行结果");
                TimeUnit.SECONDS.sleep(1);
                return 50;
            }
        });

        log.debug("等待结果");
        //log.debug("结果是 {}", future.get());

        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                // 结果已经有了 =》回调方法执行
                log.debug("接收结果:{}", future.getNow());
            }
        });
    }
}
