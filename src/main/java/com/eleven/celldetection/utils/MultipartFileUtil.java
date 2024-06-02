package com.eleven.celldetection.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.disk.DiskFileItem;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;
//
//import java.io.*;
//import java.net.URI;
//import java.net.URL;
//import java.nio.file.Files;
//
//
//public class MultipartFileUtil {
//
//    public static File getFile(String url) throws Exception {
//        //对本地文件命名
//        String fileName = url.substring(url.lastIndexOf("."),url.length());
//        File file = null;
//
//        URL urlfile;
//        InputStream inStream = null;
//        OutputStream os = null;
//        try {
//            file = File.createTempFile("fwj_url", fileName);
//            //下载
//            urlfile = new URL(url);
//            inStream = urlfile.openStream();
//            os = new FileOutputStream(file);
//
//            int bytesRead = 0;
//            byte[] buffer = new byte[8192];
//            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != os) {
//                    os.close();
//                }
//                if (null != inStream) {
//                    inStream.close();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return file;
//    }
//
//    public static MultipartFile getMultipartFile(String url) {
//        FileInputStream inputStream = null;
//        OutputStream outputStream = null;
//        try {
//            File file = getFile(url);
//            System.out.println(file.toPath());
//            FileItem fileItem = new DiskFileItem("formFieldName",//form表单文件控件的名字随便起
//                    Files.probeContentType(file.toPath()),//文件类型
//                    false, //是否是表单字段
//                    file.getName(),//原始文件名
//                    (int) file.length(),//Interger的最大值可以存储两部1G的电影
//                    file.getParentFile());//文件会在哪个目录创建
//            //为DiskFileItem的OutputStream赋值
//            inputStream = new FileInputStream(file);
//            outputStream = fileItem.getOutputStream();
//            IOUtils.copy(inputStream, outputStream);
//            return new CommonsMultipartFile(fileItem);
//        } catch (Exception e) {
////            log.error("文件类型转换失败" + e.getMessage());
//            return null;
//        } finally {
//            try {
//                if (null != inputStream) {
//                    inputStream.close();
//                }
//
//                if (null != outputStream) {
//                    outputStream.close();
//                }
//            } catch (IOException e) {
////                log.error(">>文件流关闭失败" + e.getMessage());
//            }
//        }
//
//    }
//}

//import org.apache.http.entity.ContentType;
import ch.qos.logback.core.util.FileUtil;
//import jdk.jfr.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;


public class MultipartFileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * @description:  根据文件路径，获取MultipartFile对象
     * @author: nisan
     * @date: 2022/1/18 13:08
     * @param path
     * @return org.springframework.web.multipart.MultipartFile
     */
    public static MultipartFile getMultipartFile(String path) {
        MultipartFile mFile = null;
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            String fileName = file.getName();
            fileName = fileName.substring((fileName.lastIndexOf("/") + 1));
            mFile =  new MockMultipartFile(fileName, fileName, MediaType.APPLICATION_OCTET_STREAM_VALUE.toString(), fileInputStream);
        } catch (Exception e) {
            log.error("封装文件出现错误：{}", e);
            //e.printStackTrace();
        }
        return mFile;
    }

}