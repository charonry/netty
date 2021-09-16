package com.charon.netty;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-09-16 16:19
 **/
@Slf4j
public class JdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行结果");
                TimeUnit.SECONDS.sleep(1);
                return 50;
            }
        });

        log.debug("等待结果");
        log.debug("结果是 {}", future.get());

    }
}
