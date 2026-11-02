package com.esgov.yqfk.entity.dc;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.esgov.yqfk.entity.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "dc_information_system_manage")
public class DcInformationSystemManage extends BaseEntity {

    @TableField(value = "system_name")
    private String systemName;

    /** 信息系统状态,字典:system_state */
    @TableField(value = "system_state")
    private String systemState;

    /** 部署方式,字典:deployment_method */
    @TableField(value = "deployment_method")
    private String deploymentMethod;

    /** 当前所属单位id */
    @TableField(value = "current_affiliated_unit_id")
    private String currentAffiliatedUnitId;

    /** 实施单位id */
    @TableField(value = "implementation_unit_id")
    private String implementationUnitId;

    /** 接入网络类型,字典:network_access_type */
    @TableField(value = "network_access_type")
    private String networkAccessType;

    /** 功能描述 */
    @TableField(value = "function_description")
    private String functionDescription;
}
