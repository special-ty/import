package com.esgov.yqfk.vo.dc.importVo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述: 业务系统管理导入 VO（照抄原项目 common 模块，去掉 swagger 注解）
 * 注意: 字段顺序 = Excel 列顺序，@ExcelProperty 的 index 必须与模板一致
 *
 * @author ZhangHao
 * @date 2025-07-16 16:42
 */
@Data
public class DcInformationSystemManageImportVo implements Serializable {

    /** 信息系统名称 */
    @ExcelProperty(value = "信息系统名称", index = 0)
    private String systemName;

    /** 信息系统状态 */
    @ExcelProperty(value = "信息系统状态", index = 1)
    private String systemState;

    /** 部署方式 */
    @ExcelProperty(value = "部署方式", index = 2)
    private String deploymentMethod;

    /** 当前所属单位（Excel 里填单位中文名，落库前转单位 id；字段名带 Id 为原项目历史命名） */
    @ExcelProperty(value = "当前所属单位", index = 3)
    private String currentAffiliatedUnitId;

    /** 实施单位 */
    @ExcelProperty(value = "实施单位", index = 4)
    private String implementationUnitId;

    /** 接入网络类型 */
    @ExcelProperty(value = "接入网络类型", index = 5)
    private String networkAccessType;

    /** 功能描述 */
    @ExcelProperty(value = "功能描述", index = 6)
    private String functionDescription;
}
