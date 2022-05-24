package com.ctgu.model.types;

/**
 * @Author beck_guo
 * @create 2022/5/23 11:08
 * @description
 */
public enum TaskHandleEnum {

    COMPLETE("taskComplete"),
    ADDSIGN("taskAddSign"),
    TRANSFER("taskTransferOrDelegate"),
    DELEGATE("taskTransferOrDelegate"),
    CHANGEASSIGN("taskChangeAssign"),
    ANYNODE("taskAnyNode"),
    HANGORACTIVATION("taskHangOrActivation"),
    STOP("taskStop");

    private String serviceName;

    TaskHandleEnum(String serviceName){
        this.serviceName=serviceName;
    }

    public String getServiceName(){
        return this.serviceName;
    }


    public static String getServiceByType(String typeName){
        for(TaskHandleEnum type: TaskHandleEnum.values()){
            if(type.toString().equals(typeName)){
                return type.getServiceName();
            }
        }
        return null;
    }

}
