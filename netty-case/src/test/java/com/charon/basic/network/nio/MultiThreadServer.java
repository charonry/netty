package com.charon.basic.network.nio;

import com.charon.util.ByteBufferUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-07-07 20:31
 **/
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 1. 创建固定数量的 worker 并初始化
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey scKey = iterator.next();
                iterator.remove();
                if (scKey.isAcceptable()) {
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    log.debug("contect......{}",socketChannel.getRemoteAddress());
                    // 2. 关联 selector
                    log.debug(" before contect......{}",socketChannel.getRemoteAddress());
                    workers[index.getAndIncrement()%workers.length].register(socketChannel);
                    log.debug(" after contect......{}",socketChannel.getRemoteAddress());
                }
            }
        }
    }

     static  class  Worker implements Runnable{
        private String name;
        private Thread thread;
        private Selector selector;
        private AtomicBoolean start = new AtomicBoolean(false);
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        public Worker() {
        }

        public Worker(String name) {
            this.name = name;
        }

        /**
         *  初始化线程，和 selector
         */
        public void register(SocketChannel socketChannel) throws IOException {
            if (!start.get()){
                selector = Selector.open();
                thread = new Thread(this,name);
                thread.start();
                start.set(true);
            }
            /*// 向队列添加了任务，但是这个任务并没有执行
            queue.add(()->{
                try {
                    socketChannel.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });*/
            // 唤醒select方法
            selector.wakeup();
            socketChannel.register(selector,SelectionKey.OP_READ,null);
        }

        @SneakyThrows
        @Override
        public void run() {
            while (true){
                    selector.select();
                   /* Runnable task = queue.poll();
                    if(task != null){
                        // 执行了上面queue里面的任务
                        task.run();
                    }*/
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey scKey = iterator.next();
                        iterator.remove();
                        try {
                            if (scKey.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel)scKey.channel();
                                log.debug(" read contect......{}",channel.getRemoteAddress());
                                channel.read(buffer);
                                buffer.flip();
                                ByteBufferUtil.debugAll(buffer);
                            }
                        }catch (IOException e) {
                                e.printStackTrace();
                                scKey.cancel();
                        }
                }
            }
        }
    }
}



