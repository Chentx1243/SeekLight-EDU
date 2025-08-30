package com.xshxy.seeklightbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xshxy.seeklightbackend.mapper")
public class SeeklightBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeklightBackendApplication.class, args);
    }

}
