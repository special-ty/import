package com.esgov.yqfk.mapper.dc;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述: 业务系统管理 Mapper（照抄原项目，仅保留导入相关方法 + 一个验收用列表查询）
 *
 * @author ZhangHao
 * @date 2025-07-16 14:50
 */
@Mapper
public interface DcInformationSystemManageMapper extends BaseMapper<DcInformationSystemManage> {

    /** 验收辅助：导入后查看结果（原项目该方法是带分页/条件查询，此处最小化） */
    List<DcInformationSystemManage> getSystemManageList();

    Integer getOrgCount(@Param("name") String name);

    String getOrgIdByName(@Param("name") String name);
}
