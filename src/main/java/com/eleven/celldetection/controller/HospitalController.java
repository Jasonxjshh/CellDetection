package com.eleven.celldetection.controller;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.util.PoitlIOUtils;
//import edu.neu.verificationtreasure.bean.hospital.MedicalRecord;
import com.eleven.celldetection.entity.MedicalRecord;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * <p>
 *
 * </p>
 *
 * @Author: wzp
 * @Date: 2022/12/29 16:05
 */
@RestController
public class HospitalController {

    private final ResourceLoader resourceLoader;

    public HospitalController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(path = "/medicalRecord")
    public void TalkWordOne(@RequestBody MedicalRecord medicalRecord, HttpServletResponse response) throws IOException {

        ConfigureBuilder builder = Configure.builder();
        builder.useSpringEL();

        Resource resource = resourceLoader.getResource("classpath:word/hospital/MedicalRecord.docx");
        XWPFTemplate template = XWPFTemplate.compile(resource.getInputStream(), builder.build()).render(medicalRecord);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=\"" + medicalRecord.getFile_name() + ".docx" + "\"");

        OutputStream out = response.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(out);
        template.write(bos);
        bos.flush();
        out.flush();
        PoitlIOUtils.closeQuietlyMulti(template, bos, out);

    }
}
