package com.esgov.yqfk.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esgov.yqfk.core.dto.ServiceResponse;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import com.esgov.yqfk.listener.DcInformationSystemManageImportListener;
import com.esgov.yqfk.mapper.dc.DcInformationSystemManageMapper;
import com.esgov.yqfk.service.IDcInformationSystemManageService;
import com.esgov.yqfk.vo.dc.importVo.DcInformationSystemManageImportVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述: 业务系统管理服务实现（照抄原项目 importSystem 方法体，去掉用户参数）
 *
 * @author ZhangHao
 * @date 2025-07-16 14:51
 */
@Service
@Slf4j
public class DcInformationSystemManageServiceImpl extends ServiceImpl<DcInformationSystemManageMapper, DcInformationSystemManage> implements IDcInformationSystemManageService {

    @Resource
    private DcInformationSystemManageMapper manageMapper;

    @Override
    public ServiceResponse<List<DcInformationSystemManage>> getSystemManageList() {
        List<DcInformationSystemManage> systemManageList = manageMapper.getSystemManageList();
        return ServiceResponse.createSuccessByData(systemManageList);
    }

    @Override
    public ServiceResponse importSystem(MultipartFile file) {
        try {
            String name = file.getOriginalFilename();
            String substring = name.substring(name.lastIndexOf(".") + 1);
            if (!substring.equals("xlsx") && !substring.equals("xls")) {
                return ServiceResponse.createErrorByMsg("文件格式有误!请重新导入!");
            }
            EasyExcel.read(file.getInputStream(), DcInformationSystemManageImportVo.class, new DcInformationSystemManageImportListener()).sheet().doRead();
            return ServiceResponse.createSuccessByData("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.createErrorByMsg(e.getMessage());
        }
    }
}
