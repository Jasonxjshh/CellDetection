package com.eleven.celldetection.DetectionController;

import com.alibaba.fastjson.JSONObject;
import com.eleven.celldetection.DetectionService.DetectionService;
import com.eleven.celldetection.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/detect")

public class DetectionController {

    @Autowired
    private DetectionService detectionService;

    @PostMapping("/get")
    @ResponseBody
    public Result detectCells(@RequestParam("file") MultipartFile file) {
        Result result = new Result();
        try {
            boolean empty = file.isEmpty();
            String name = file.getName();
            byte[] bytes = file.getBytes();

            JSONObject detections = detectionService.detect(file);

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

}

