package com.esgov.yqfk.core.dto;

import com.esgov.yqfk.core.dto.enums.ResponseCode;

import java.io.Serializable;

/**
 * 描述: 统一返回包装（精简自原项目 common 模块的 ServiceResponse，
 * 去掉 fastjson / requestId 依赖，保留 code/desc/data/message 与工厂方法）
 *
 * @author Yangjinming
 * @create 2018/3/26 21:31
 */
public class ServiceResponse<T> implements Serializable {
    private static final long serialVersionUID = -4012170306337511959L;

    /**
     * 返回状态 0成功 -1错误
     */
    private int code;
    /**
     * 访问描述
     */
    private String desc;
    /**
     * 返回数据
     */
    private T data;

    private String message;

    public ServiceResponse() {
    }

    private ServiceResponse(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private ServiceResponse(int code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public static <T> ServiceResponse<T> createSuccess() {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg());
    }

    public static <T> ServiceResponse<T> createSuccessByData(T data) {
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), data);
    }

    public static <T> ServiceResponse<T> createError() {
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
    }

    public static <T> ServiceResponse<T> createErrorByMsg(String msg) {
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
