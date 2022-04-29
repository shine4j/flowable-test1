package com.ctgu.service;

import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:38
 * @description
 */
public interface IModerService {

    void getImageById(@PathVariable String modeId, HttpServletResponse resp) throws IOException;

    void getXmlById(@PathVariable String modeId, HttpServletResponse resp);
}
