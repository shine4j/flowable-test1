package com.ctgu.util;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/4/12 13:46
 * @description
 */
@Component
public class FlowProcessDiagramGenerator extends DefaultProcessDiagramGenerator {

    private static final String IMAGE_TYPE = "png";

    private String activityFontName="宋体";
    private String labelFontName="宋体";
    private String annotationFontName="宋体";

    /**
     * 生成图片流
     *
     * @param bpmnModel             模型
     * @param highLightedActivities 活动节点
     * @param highLightedFlows      高亮线
     * @return
     */
    public InputStream generateDiagram(BpmnModel bpmnModel, List<String> highLightedActivities, List<String> highLightedFlows) {
        return generateDiagram(bpmnModel, IMAGE_TYPE, highLightedActivities,
                highLightedFlows, activityFontName, labelFontName, annotationFontName,
                null, 1.0, true);
    }

    /**
     * 生成图片流
     *
     * @param bpmnModel 模型
     * @return
     */
    public InputStream generateDiagram(BpmnModel bpmnModel) {
        return generateDiagram(bpmnModel, IMAGE_TYPE, activityFontName,
                labelFontName, annotationFontName,
                null, 1.0, true);
    }
}
