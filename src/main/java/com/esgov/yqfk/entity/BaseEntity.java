package com.esgov.yqfk.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述: 实体基类（照抄原项目 common 模块，去掉 swagger 注解）
 * Created by liqiao on 2020/3/15.
 */
@Data
public class BaseEntity implements Serializable {

    private String id;

    private String status = "1";

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
