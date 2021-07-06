package com.charon.basic.network.nio;

import com.charon.basic.example.TestByteBufferExam;
import com.charon.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-07-01 20:33
 **/
@Slf4j
public class Server {

    public static void split(ByteBuffer source) {
        source.flip();
        for(int i =0;i<source.limit();i++){
            // 找到一条完整消息
            if(source.get(i)=='\n'){
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for(int j =0;j<length;j++){
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }
    public static void main(String[] args) throws IOException {
        // 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc =  ServerSocketChannel.open();
        ssc.configureBlocking(false);
        // 2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey selectionKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("selectionKey:{}", selectionKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true){
            // 3. select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行
            // select 在事件未处理时，它不会阻塞, 事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                log.debug("key: {}", key);
                // 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iterator.remove();
                // 5. 区分事件类型
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    // 将bytebuffer作为附件关联到SelectionKey上
                    SelectionKey scKey = socketChannel.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("socketChannel: {}", socketChannel);
                    log.debug("scKey: {}", scKey);
                    /*key.cancel();*/
                }else if(key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel)key.channel();
                        // 获取SelectionKey上附件
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer);
                        if(read == -1){
                            key.cancel();
                        }else {
                            split(buffer);
                            if(buffer.limit() == buffer.capacity()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() << 1);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        // 因为客户端断开了,因此需要将 key 取消（从 selector 的 keys 集合中真正删除 key）
                        key.cancel();
                    }
                }

            }
        }
    }

    /**
     * 没有选择器的通信模式 ----(非)阻塞模式
     * @throws IOException
     */
    private static void noSelectorCommunication() throws IOException {
        // 使用 nio 来理解阻塞模式, 单线程
        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 创建了服务器
        ServerSocketChannel ssc =  ServerSocketChannel.open();
        // 非阻塞模式
        ssc.configureBlocking(false);
        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        // 3. 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true){
            // 4. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信
            /*log.debug("connecting...");*/
            // 阻塞方法，线程停止运行
            // 非阻塞模式下线程还会运行 如果没有建立链接 sc为null
            SocketChannel sc = ssc.accept();
            if(sc != null){
                log.debug("connected... {}", sc);
                // 非阻塞模式
                sc.configureBlocking(false);
                channels.add(sc);
            }
            for(SocketChannel channel : channels){
                // 5. 接收客户端发送的数据
                /*log.debug("before read... {}", channel);*/
                // 阻塞方法，线程停止运行
                // 非阻塞模式下线程还会运行 如果没有读到数据read返回0
                int readLength = channel.read(buffer);
                if(readLength > 0){
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }
            }
        }
    }

}
