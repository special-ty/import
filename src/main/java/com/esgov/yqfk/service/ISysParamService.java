package com.esgov.yqfk.service;

/**
 * 描述: 字典参数服务（原项目为 IRedisSysParamService，只查 Redis 缓存不回源；
 * 演示项目为最小运行改为直接查库，等价于原项目缺失的"回源"兜底逻辑）
 */
public interface ISysParamService {

    /**
     * 按字典类型 + 参数值取字典码
     */
    String getCodeByValue(String paramTypeCode, String value);
}
