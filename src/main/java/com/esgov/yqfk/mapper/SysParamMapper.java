package com.esgov.yqfk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 描述: 字典参数 Mapper
 */
@Mapper
public interface SysParamMapper {

    /**
     * 按字典类型 + 参数值取字典码
     */
    String getCodeByValue(@Param("paramTypeCode") String paramTypeCode, @Param("value") String value);
}
