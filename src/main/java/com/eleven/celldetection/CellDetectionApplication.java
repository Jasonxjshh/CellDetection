package com.eleven.celldetection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@MapperScan(basePackages = "com.eleven.celldetection.mapper")
@CrossOrigin
public class CellDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CellDetectionApplication.class, args);
    }

}
