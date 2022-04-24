package com.ctgu.web;

import com.ctgu.util.FlowProcessDiagramGenerator;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @Author beck_guo
 * @create 2022/4/12 13:38
 * @description
 */
@RestController
public class ModerController {

    @Autowired
    protected ModelService modelService;


    @Autowired
    private FlowProcessDiagramGenerator flowProcessDiagramGenerator;

    @GetMapping(value = "/loadPngByModelId/{modeId}")
    public void image(@PathVariable String modeId, HttpServletResponse response) throws IOException {
        Model model = modelService.getModel(modeId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model, new HashMap<>(), new HashMap<>());
        InputStream inputStream = flowProcessDiagramGenerator.generateDiagram(bpmnModel, "png", "宋体",
                "宋体", "宋体",
                null, 1.0, true);
        response.reset();
        response.setHeader("Content-Type", "image/png");
        byte[] b = new byte[1024];
        int len;
        while (true) {
            try {
                if (!((len = inputStream.read(b, 0, 1024)) != -1)) {
                    break;
                }
                response.getOutputStream().write(b, 0, len);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @GetMapping("/loadXmlByModelId/{modeId}")
    public void loadXmlByModelId(@PathVariable String modeId, HttpServletResponse response){
        Model model = modelService.getModel(modeId);
        byte[] bpmnXML = modelService.getBpmnXML(model);
        response.reset();
        response.setHeader("Content-type", "text/xml;charset=UTF-8");
        try {
            response.getOutputStream().write(bpmnXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
