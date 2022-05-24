package com.ctgu.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/5/24 10:29
 * @description
 */
@Service
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext=context;
    }

    public static <T> T popBean(Class<T> clazz){
        if(applicationContext ==null){
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    public static <T> T popBean(String name,Class<T> clazz){
        if(applicationContext==null){
            return null;
        }
        return applicationContext.getBean(name,clazz);
    }

    public static <T> T popBean(String name){
        if(applicationContext==null){
            return null;
        }
        return (T) applicationContext.getBean(name);
    }
}
