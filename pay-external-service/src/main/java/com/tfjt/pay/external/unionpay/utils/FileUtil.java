package com.tfjt.pay.external.unionpay.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    /**
     * MultipartFile 转换为 File 文件
     *
     * @param multipartFile
     * @return
     */
    public static File transferToFile(MultipartFile multipartFile) {
        //选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            Long name= System.currentTimeMillis();
            file = File.createTempFile(String.valueOf(name), filename[1]);
            //注意下面的 特别注意！！！

            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
