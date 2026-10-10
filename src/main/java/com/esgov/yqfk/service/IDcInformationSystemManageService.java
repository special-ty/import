package com.esgov.yqfk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.esgov.yqfk.core.dto.ServiceResponse;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 描述: 业务系统管理服务（照抄原项目接口，去掉用户相关参数）
 */
public interface IDcInformationSystemManageService extends IService<DcInformationSystemManage> {

    ServiceResponse<List<DcInformationSystemManage>> getSystemManageList();

    ServiceResponse importSystem(MultipartFile file);
}
