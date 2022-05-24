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
    CHANGEASSIGN("a"),
    ANYNODE("taskAnyNode"),
    HANG("a"),
    ACTIVATION("a"),
    STOP("a");


    private String serviceName;

    TaskHandleEnum(String serviceName){
        this.serviceName=serviceName;
    }

    public String getServiceName(){
        return this.serviceName;
    }

    public static TaskHandleEnum getTypeByServiceName(String serviceName){
        for(TaskHandleEnum type: TaskHandleEnum.values()){
            if(type.getServiceName().equals(serviceName)){
                return type;
            }
        }
        return null;
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
