package com.ctgu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.swing.*;

/**
 * @Author beck_guo
 * @create 2022/4/11 10:03
 * @description
 */
@MapperScan(basePackages = "com.ctgu.dao")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ActiviApplicaiton {

    public static void main(String[] args) {
        SpringApplication.run(ActiviApplicaiton.class);
    }
}
