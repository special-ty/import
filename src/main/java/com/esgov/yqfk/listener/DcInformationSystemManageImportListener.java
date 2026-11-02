package com.esgov.yqfk.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import com.esgov.yqfk.mapper.dc.DcInformationSystemManageMapper;
import com.esgov.yqfk.service.IDcInformationSystemManageService;
import com.esgov.yqfk.service.ISysParamService;
import com.esgov.yqfk.util.SpringContextUtil;
import com.esgov.yqfk.util.UUIDUtil;
import com.esgov.yqfk.vo.dc.importVo.DcInformationSystemManageImportVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class DcInformationSystemManageImportListener extends AnalysisEventListener<DcInformationSystemManageImportVo> {

    /** 原项目从登录态取当前用户，演示项目按需求硬编码 */
    private static final String DEFAULT_USER_ID = "1";

    private List<DcInformationSystemManageImportVo> importVoList = new ArrayList<>();

    /** 本批次已出现的名称,防止Excel内部两行同名都按新增插入 */
    private Set<String> batchNames = new HashSet<>();

    private ISysParamService sysParamService;
    private IDcInformationSystemManageService manageService;
    private DcInformationSystemManageMapper manageMapper;

    public DcInformationSystemManageImportListener() {
        this.sysParamService = SpringContextUtil.getBean(ISysParamService.class);
        this.manageService = SpringContextUtil.getBean(IDcInformationSystemManageService.class);
        this.manageMapper = SpringContextUtil.getBean(DcInformationSystemManageMapper.class);
    }

    @SneakyThrows
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        List<String> headList = this.getExcelHead();
        String errorMsg = "";
        for (int i = 0; i < headList.size(); i++) {
            if (!headList.get(i).equals(headMap.get(i))) {
                String column = headMap.get(i) == null ? "为空" : headMap.get(i);
                errorMsg += "第" + (i + 1) + "列" + column + "与模板" + headList.get(i) + "不一致;";
            }
        }
        if (StringUtils.isNotBlank(errorMsg)) {
            this.onException(new Exception(errorMsg), context);
        }
    }

    @SneakyThrows
    @Override
    public void invoke(DcInformationSystemManageImportVo data, AnalysisContext context) {
        //数据校验
        String s = this.dataVerification(data, context);
        if (StringUtils.isNotBlank(s)) {
            this.onException(new Exception(s), context);
        } else {
            importVoList.add(data);
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        super.onException(exception, context);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(importVoList)) {
            this.saveBatch();
        }
    }

    private List<String> getExcelHead() {
        List<String> data = new ArrayList<>();
        Class<DcInformationSystemManageImportVo> cs = DcInformationSystemManageImportVo.class;
        Field[] declaredFields = cs.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
            if (annotation != null) {
                String[] value = annotation.value();
                if (!value[0].equals("入库状态") && !value[0].equals("失败原因")) {
                    data.add(value[0]);
                }
            }
        }
        return data;
    }

    private String dataVerification(DcInformationSystemManageImportVo data, AnalysisContext context) {
        StringBuffer sb = new StringBuffer();
        Integer rowIndex = context.readRowHolder().getRowIndex();

        if (StringUtils.isBlank(data.getSystemName())) {
            sb.append("第").append(rowIndex).append("行信息系统名称不能为空!");
        }

        if (StringUtils.isBlank(data.getSystemState())) {
            sb.append("第").append(rowIndex).append("行信息系统状态不能为空!");
        }

        if (StringUtils.isBlank(data.getDeploymentMethod())) {
            sb.append("第").append(rowIndex).append("行部署方式不能为空!");
        }

        if (StringUtils.isBlank(data.getCurrentAffiliatedUnitId())) {
            sb.append("第").append(rowIndex).append("行当前所属单位不能为空!");
        }

        if (StringUtils.isBlank(data.getImplementationUnitId())) {
            sb.append("第").append(rowIndex).append("行实施单位不能为空!");
        }

        if (StringUtils.isBlank(data.getNetworkAccessType())) {
            sb.append("第").append(rowIndex).append("行接入网络类型不能为空!");
        }

        if (StringUtils.isBlank(data.getFunctionDescription())) {
            sb.append("第").append(rowIndex).append("行功能描述不能为空!");
        }

        if (StringUtils.isNotBlank(sb)) {
            return sb.toString();
        }

        //导入只允许新增：库中同名或表格内重名均报错，整份文件不入库，不修改原有数据
        Integer existCount = manageMapper.selectCount(
                Wrappers.lambdaQuery(DcInformationSystemManage.class)
                        .eq(DcInformationSystemManage::getStatus, "1")
                        .eq(DcInformationSystemManage::getSystemName, data.getSystemName())
        );
        if (existCount != null && existCount > 0) {
            sb.append("第").append(rowIndex).append("行信息系统名称已存在!导入只支持新增数据,不能覆盖或修改原有数据;");
        } else if (!batchNames.add(data.getSystemName())) {
            sb.append("第").append(rowIndex).append("行信息系统名称在表格中重复!");
        }

        //当前所属单位校验,实施单位校验
        Integer currentAffiliatedUnit = manageMapper.getOrgCount(data.getCurrentAffiliatedUnitId());
        if (currentAffiliatedUnit <= 0) {
            sb.append("第").append(rowIndex).append("行当前所属单位不存在!");
        }
        Integer implementationUnit = manageMapper.getOrgCount(data.getImplementationUnitId());
        if (implementationUnit <= 0) {
            sb.append("第").append(rowIndex).append("行实施单位不存在!");
        }
        return sb.toString();
    }

    private void saveBatch() {
        log.info("=======开始进行数据插入工作=========");
        List<DcInformationSystemManage> insertList = new ArrayList<>();
        for (DcInformationSystemManageImportVo importVo : importVoList) {
            try {
                DcInformationSystemManage management = new DcInformationSystemManage();
                management.setSystemName(importVo.getSystemName());
                management.setFunctionDescription(importVo.getFunctionDescription());
                management.setSystemState(sysParamService.getCodeByValue("system_state", importVo.getSystemState()));
                management.setDeploymentMethod(sysParamService.getCodeByValue("deployment_method", importVo.getDeploymentMethod()));
                management.setNetworkAccessType(sysParamService.getCodeByValue("network_access_type", importVo.getNetworkAccessType()));
                management.setCurrentAffiliatedUnitId(manageMapper.getOrgIdByName(importVo.getCurrentAffiliatedUnitId()));
                management.setImplementationUnitId(manageMapper.getOrgIdByName(importVo.getImplementationUnitId()));

                management.setId(UUIDUtil.getUUID());
                management.setStatus("1");
                management.setCreator(DEFAULT_USER_ID);
                management.setCreateTime(new Date());
                management.setUpdateUser(DEFAULT_USER_ID);
                management.setUpdateTime(new Date());
                insertList.add(management);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            manageService.saveBatch(insertList);
        }
    }
}
