package com.esgov.yqfk.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.esgov.yqfk.mapper.SysParamMapper;
import com.esgov.yqfk.service.ISysParamService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 描述: 字典参数服务实现（直接查库版）
 */
@Service
public class SysParamServiceImpl implements ISysParamService {

    @Resource
    private SysParamMapper sysParamMapper;

    @Override
    public String getCodeByValue(String paramTypeCode, String value) {
        if (StringUtils.isBlank(paramTypeCode) || StringUtils.isBlank(value)) {
            return null;
        }
        return sysParamMapper.getCodeByValue(paramTypeCode, value);
    }
}
