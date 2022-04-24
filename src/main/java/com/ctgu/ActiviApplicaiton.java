package com.ctgu;

import com.ctgu.config.AppDispatcherServletConfiguration;
import com.ctgu.config.ApplicationConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

import javax.swing.*;

/**
 * @Author beck_guo
 * @create 2022/4/11 10:03
 * @description
 */
@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@MapperScan(basePackages = "com.ctgu.dao")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ActiviApplicaiton {

    public static void main(String[] args) {
        SpringApplication.run(ActiviApplicaiton.class);
    }
}
