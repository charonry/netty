package com.charon.basic.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardCopyOption;

/**
 * @program: netty
 * @description
 * @author: charon
 * @create: 2021-06-30 20:45
 **/
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("netty-case\\src\\main\\resources\\data.txt").getChannel();
             FileChannel to = new FileOutputStream("netty-case\\src\\main\\resources\\to.txt").getChannel();) {
            // 效率高，底层会利用操作系统的零拷贝进行优化 传输上限为2g
            long size = from.size();
            // left 变量代表还剩余多少字节
            for(long left = size;left>0;){
                System.out.println("position:" + (size - left) + " left:" + left);
                left -= from.transferTo((size-left),left,to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
