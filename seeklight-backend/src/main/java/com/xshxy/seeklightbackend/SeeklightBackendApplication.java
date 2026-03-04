package com.xshxy.seeklightbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@MapperScan("com.xshxy.seeklightbackend.mapper")
@ConfigurationPropertiesScan("com.xshxy.seeklightbackend.config")
public class SeeklightBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeklightBackendApplication.class, args);
    }

}
