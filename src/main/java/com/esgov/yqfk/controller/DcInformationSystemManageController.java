package com.esgov.yqfk.controller;

import com.esgov.yqfk.core.dto.ServiceResponse;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import com.esgov.yqfk.service.IDcInformationSystemManageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述: 业务系统管理控制器（照抄原项目，去掉 swagger 注解与用户信息获取）
 *
 * @author ZhangHao
 * @date 2025-07-16 14:49
 */
@RestController
@RequestMapping("/system/manage/")
public class DcInformationSystemManageController {
    @Resource
    private IDcInformationSystemManageService manageService;

    @PostMapping("importSystem")
    public ServiceResponse importSystem(@RequestParam("file") MultipartFile file) throws Exception {
        return manageService.importSystem(file);
    }

    /** 验收辅助接口：导入后查看列表（原项目为 POST + 分页条件查询，此处最小化） */
    @GetMapping("getSystemManageList")
    public ServiceResponse<List<DcInformationSystemManage>> getSystemManageList() {
        return manageService.getSystemManageList();
    }
}
