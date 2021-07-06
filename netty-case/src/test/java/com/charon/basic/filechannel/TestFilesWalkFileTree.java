package com.charon.basic.filechannel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: netty
 * @description 对于文件夹的遍历处理方式
 * @author: charon
 * @create: 2021-06-30 21:16
 **/
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        //foreachFile();
        foreachSpecial();
        //foreachDelete();
        return;
    }

    /**
     * 遍历删除文件夹
     * @throws IOException
     */
    private static void foreachDelete() throws IOException {
        Files.walkFileTree(Paths.get("netty-case\\src\\main\\resources\\file"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    /**
     * 获取特定文件
     * @throws IOException
     */
    private static void foreachSpecial() throws IOException {
        String path = "D:\\JDK\\jdk1.8.0_171";
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get(path),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jar count:" +jarCount);
    }

    /**
     * 遍历获取文件个数
     * @throws IOException
     */
    private static void foreachFile() throws IOException {
        String path = "D:\\JDK\\jdk1.8.0_171";
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        /**
         * 访问者模式
         */
        Files.walkFileTree(Paths.get(path),new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>"+dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dir count:" +dirCount);
        System.out.println("file count:" +fileCount);
    }
}
