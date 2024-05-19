package com.eleven.celldetection.controller;


import com.alibaba.fastjson.annotation.JSONField;
import com.eleven.celldetection.entity.Img;
//import com.eleven.celldetection.mapper.ImgMapper;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import org.springframework.web.bind.annotation.RestController;
import com.eleven.celldetection.mapper.ImgMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.alibaba.fastjson.JSON;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pjqdyd
 * @since 2024-05-12
 */
@RestController
@CrossOrigin
@RequestMapping("/api/img")
public class ImgController {

    @Resource
    private com.eleven.celldetection.mapper.ImgMapper imgMapper;

    @CrossOrigin
    @RequestMapping("/img")
    public List<Img> img() {
        System.out.println(("----- selectAll method test ------"));
        List<Img> imgList = imgMapper.selectList(null);
//        Assert.assertEquals(5, userList.size());
        return imgList ;
    }


//    @Resource
//    private ImgMapper imgMapper;

//    private final ImgMapper imgMapper;

    private static final String path = "D:\\";

    protected ImgController(ImgMapper imgMapper) {
        this.imgMapper = imgMapper;
    }

//    @JSONField(serialize = false)
    @PostMapping("/fileUpload")
    public String uploadFile(MultipartFile file) {

        HashMap<String, Object> data = new HashMap<>();

        if (file.isEmpty()) {
            data.put("state", "请选择文件");
            return JSON.toJSONString(data);
        }

        Img img = new Img();

        try {

            Date date = new Date();

            String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(date);
            data.put("time", time);
            img.setTime(time);

            // 获取文件名
            String fileName = "(" + new SimpleDateFormat("yyyyMMdd-HHmmss-S").format(date) + ")" + file.getOriginalFilename();

            data.put("fileName", fileName);
            data.put("file", file.getOriginalFilename());
            img.setFile_name(fileName);

            // 设置文件存储路径
//            String filePath = path + fileName;


            // 设置文件存储路径
            String filePath = ".\\save_pic\\" + fileName;

            File dest = new File(filePath);

            data.put("filePath", filePath);
            img.setFilePath(filePath);

            // 如果目录不存在则创建
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    return "false";
                }
            }
            // 将上传文件保存到目标文件中
            file.transferTo(dest);
            data.put("state", "文件上传成功");
//            img.setUserid("文件上传成功");

        } catch (IOException e) {
            data.put("state", "文件上传失败");
//            img.setUserid("文件上传失败");
        } finally {
            imgMapper.insert(img);
        }

        return JSON.toJSONString(data);

    }


    //    @JSONField(serialize = false)
    @PostMapping("/user_up_img")
    public String user_up_img( @RequestParam MultipartFile file , @RequestParam String user_id  ) {

        Integer userid = Integer.parseInt(user_id) ;

        HashMap<String, Object> data = new HashMap<>();

        if (file.isEmpty()) {
            data.put("userid", user_id);
            return JSON.toJSONString(data);
        }

        Img img = new Img();

        try {

            Date date = new Date();

            String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(date);
            data.put("time", time);
            img.setTime(time);

            // 获取文件名
            String fileName = "(" + new SimpleDateFormat("yyyyMMdd-HHmmss-S").format(date) + ")" + file.getOriginalFilename();

            data.put("fileName", fileName);
            data.put("file", file.getOriginalFilename());
            img.setFile_name(fileName);

            // 设置文件存储路径
            String filePath = path + fileName;


            // 设置文件存储路径
//            String filePath = ".\\save_pic\\" + fileName;

            File dest = new File(filePath);

            data.put("filePath", dest.getPath());
            img.setFilePath(dest.getPath());

            // 如果目录不存在则创建
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    return "false";
                }
            }
            // 将上传文件保存到目标文件中
            file.transferTo(dest);
            data.put("userid", userid);
            img.setUserid(userid);

        } catch (IOException e) {
            data.put("userid", userid);
            img.setUserid(userid);
        } finally {
            imgMapper.insert(img);
        }

        return JSON.toJSONString(data);

    }





}
