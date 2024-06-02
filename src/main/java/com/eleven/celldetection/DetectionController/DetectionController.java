package com.eleven.celldetection.DetectionController;

import com.alibaba.fastjson.JSONObject;
import com.eleven.celldetection.DetectionService.DetectionService;
import com.eleven.celldetection.mapper.ImgMapper;
import com.eleven.celldetection.util.Result;
import com.eleven.celldetection.utils.MultipartFileUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/detect")

public class DetectionController {

    @Autowired
    private DetectionService detectionService ;

//    @Resource
    private static JSONObject detections = null;

    @Resource
    private ImgMapper imgMapper ;


    @PostMapping("/get")
    @ResponseBody
    public Result detectCells(@RequestParam("file") MultipartFile file) {
        Result result = new Result();
        try {
            boolean empty = file.isEmpty();
            String name = file.getName();
            byte[] bytes = file.getBytes();

            JSONObject detections = null;

            result.setCode(200);
            result.setData(detections);
            result.setMessage("success");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage()).setCode(500);
            return result;
        }
    }


    @PostMapping("/get_from_filepath")
    @ResponseBody
    public Result detectCells_filepaeth(@RequestParam  String userid ) {

        detections = null ;

        resetCounts() ;

        Result result = new Result();

        Integer user_id = Integer.parseInt(userid) ;

        List<String> filepaths = imgMapper.getFilepathByUserid ( user_id ) ;

        String userName = imgMapper.getUserNameByUserid ( user_id ) ;

        Integer age = imgMapper.getageByUserid ( user_id ) ;

//        JSONObject detections = null ;

        try {
            for ( int i = 0 ; i < filepaths.size() ; i ++ ) {

                MultipartFile file = MultipartFileUtil.getMultipartFile(filepaths . get(i));
                boolean empty = file.isEmpty();
                String name = file.getName();
                byte[] bytes = file.getBytes();

                detections = detectionService.detect(file , userName , age  );


                result.setCode(200);
                result.setData(detections);
                result.setMessage("success");


            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage()).setCode(500);
            return result;
        }
//        return result ;
    }

    @GetMapping("/download-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> generateReport(  ) throws Exception {
        String base64Image = detections.getString("image");
        if (base64Image.startsWith("data:image")) {
            // 查找逗号之后的部分，这通常是实际的Base64编码数据
            int commaIndex = base64Image.indexOf(',');
            if (commaIndex != -1) {
                base64Image = base64Image.substring(commaIndex + 1);
            }
        }
        BufferedImage image = detectionService.convertBase64ToImage(base64Image);
        String UserName = detections.getString("userName");
//        Integer age = detections.getInteger("age");
        ByteArrayInputStream bis = detectionService.createPdfReport(image , UserName );

        byte[] bytes = toByteArray(bis);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.pdf");


        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

//    @PostMapping("/clear")
    public ResponseEntity<String> resetCounts() {
        detectionService.resetCounts();
        return ResponseEntity.ok("Counts have been reset");
    }

    private byte[] toByteArray(ByteArrayInputStream bis) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024]; // Buffer size, can be tuned as needed

        while ((nRead = bis.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }


}

