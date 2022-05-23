package com.ctgu.service.impl;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.service.IModerService;
import com.ctgu.util.FlowProcessDiagramGenerator;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:40
 * @description
 */
@Service
public class ModerServiceImpl implements IModerService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ModelService modelService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private FlowProcessDiagramGenerator flowProcessDiagramGenerator;


    @Override
    public void getImageById(String modeId, HttpServletResponse resp) throws IOException {
        Model model = modelService.getModel(modeId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model, new HashMap<>(), new HashMap<>());
        InputStream inputStream = flowProcessDiagramGenerator.generateDiagram(bpmnModel, "png", "宋体",
                "宋体", "宋体",
                null, 1.0, true);
        resp.reset();
        resp.setHeader("Content-Type", "image/png");
        byte[] b = new byte[1024];
        int len;
        while (true) {
            try {
                if (!((len = inputStream.read(b, 0, 1024)) != -1)) {
                    break;
                }
                resp.getOutputStream().write(b, 0, len);
            } catch (Exception e) {
                logger.error("取流程图片报错:{}",e);
            }

        }
    }

    @Override
    public void getXmlById(String modeId, HttpServletResponse resp) {
        Model model = modelService.getModel(modeId);
        byte[] bpmnXML = modelService.getBpmnXML(model);
        resp.reset();
        resp.setHeader("Content-type", "text/xml;charset=UTF-8");
        try {
            resp.getOutputStream().write(bpmnXML);
        } catch (IOException e) {
            logger.error("取流程图xml报错:{}",e);
        }
    }

    @Override
    public ResultMsgBO getAllModer() {
        List<AbstractModel> list = modelService.getModelsByModelType(0);
        List<Map<String,Object>> moders = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("modelerId",o.getId());
                    map.put("ModelKey",o.getKey());
                    map.put("modelerName",o.getName());
                    moders.add(map);
                });
        return new ResultMsgBO(0,"ok",moders);
    }

    @Override
    public ResultMsgBO deployModerById(String moderId) {
        Model model = modelService.getModel(moderId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deploy = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getDescription())
                .addBpmnModel(model.getKey() + ".bpmn", bpmnModel)
                .deploy();
        Map<String,Object> reModel =  new HashMap<>();
        reModel.put("modelerId",deploy.getId());
        reModel.put("ModelKey",deploy.getKey());
        reModel.put("modelerName",deploy.getName());
        return new ResultMsgBO(0,"ok",reModel);
    }
}
