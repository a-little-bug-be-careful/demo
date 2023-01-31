package com.example.demo2.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class FileUtil {

    //文件转为字节数组
    public static byte[] fileToBytes(File file, String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            file = new File(filePath);
        }
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (Exception ex) {

        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {

            } finally{
                try {
                    if(null!=fis){
                        fis.close();
                    }
                } catch (IOException ex) {

                }
            }
        }
        return buffer;
    }
}
