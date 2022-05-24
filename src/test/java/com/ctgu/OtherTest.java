package com.ctgu;

import com.ctgu.model.types.TaskHandleEnum;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author beck_guo
 * @create 2022/5/23 11:18
 * @description
 */
@SpringBootTest
public class OtherTest {

    Logger logger= LoggerFactory.getLogger(getClass());

    public void t1(){
        logger.info("==={}", TaskHandleEnum.getServiceByType("COMPLETE"));
    }

    @Test
    public void test(){
        t1();
    }
}
