package com.ubin.stf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.ubin.stf.mapper")
public class StfApplication {

    public static void main(String[] args) {
        SpringApplication.run(StfApplication.class, args);
    }

}
