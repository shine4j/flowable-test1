package com.ctgu.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beck_guo
 * @create 2022/6/28 10:17
 * @description
 */
@RestController
@RequestMapping("test")
public class TestController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public String index() throws InterruptedException {

        Thread.sleep(20000);
        logger.info("==========");
        return "ok11";
    }
}
