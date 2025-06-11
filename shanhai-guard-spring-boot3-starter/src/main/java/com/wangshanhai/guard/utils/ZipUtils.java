package com.wangshanhai.guard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static List<String> readZipFile(String path) {
        List<String> respList = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                //解决包内文件存在中文时的中文乱码问题
                fileInputStream = new FileInputStream(path);
                respList = readZipFile(fileInputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return respList;
    }

    public static List<String> readZipFile(InputStream inputStream) {
        List<String> respList = new ArrayList<>();
        ZipEntry zipEntry = null;
        ZipInputStream zipInputStream = null;
        try {
            //解决包内文件存在中文时的中文乱码问题
            zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                //遇到文件夹就跳过
                if (zipEntry.isDirectory()) {
                    continue;
                } else {
                    respList.add(zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return respList;
    }

    public static boolean checkZipFiles(List<String> zipFiles, String safeSuffixs) {
        List<String> safeSuffixlist = Arrays.asList(safeSuffixs.split(","));
        for (String fileName : zipFiles) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!safeSuffixlist.contains(suffix.trim().toLowerCase())) {
                return false;
            }
        }
        return true;
    }


}
