package com.charon.basic.filechannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @program: netty
 * @description 多文件多级目录拷贝
 * @author: charon
 * @create: 2021-06-30 23:06
 **/
public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source = "netty-case\\src\\main\\resources";
        String target = "D:\\file";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 是目录
                if(Files.isDirectory(path)){
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是普通文件
                else if(Files.isRegularFile(path)){
                    Files.copy(path, Paths.get(targetName));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
