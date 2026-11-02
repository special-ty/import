package com.esgov.yqfk;

import com.alibaba.excel.EasyExcel;
import com.esgov.yqfk.core.dto.ServiceResponse;
import com.esgov.yqfk.entity.dc.DcInformationSystemManage;
import com.esgov.yqfk.mapper.dc.DcInformationSystemManageMapper;
import com.esgov.yqfk.service.IDcInformationSystemManageService;
import com.esgov.yqfk.service.ISysParamService;
import com.esgov.yqfk.service.impl.DcInformationSystemManageServiceImpl;
import com.esgov.yqfk.util.SpringContextUtil;
import com.esgov.yqfk.vo.dc.importVo.DcInformationSystemManageImportVo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 使用真实 Excel 解析和 Service/Listener，隔离数据库，不需要启动应用或 MySQL。 */
public class ImportSystemTest {
    private static final DcInformationSystemManageMapper mapper = mock(DcInformationSystemManageMapper.class);
    private static final IDcInformationSystemManageService persistence = mock(IDcInformationSystemManageService.class);
    private static final ISysParamService dictionary = mock(ISysParamService.class);
    private final DcInformationSystemManageServiceImpl service = new DcInformationSystemManageServiceImpl();

    @BeforeClass
    public static void initContext() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.getBeanFactory().registerSingleton("manageMapper", mapper);
        context.getBeanFactory().registerSingleton("manageService", persistence);
        context.getBeanFactory().registerSingleton("sysParamService", dictionary);
        context.refresh();
        new SpringContextUtil().setApplicationContext(context);
    }

    @Before
    public void setup() {
        reset(mapper, persistence, dictionary);
        when(mapper.selectCount(any())).thenReturn(0);
        when(mapper.getOrgCount(anyString())).thenReturn(1);
        when(mapper.getOrgIdByName("广州市机构")).thenReturn("-1");
        when(dictionary.getCodeByValue("system_state", "已建")).thenReturn("2");
        when(dictionary.getCodeByValue("deployment_method", "县区部署")).thenReturn("1");
        when(dictionary.getCodeByValue("network_access_type", "政务内网")).thenReturn("1");
        when(persistence.saveBatch(anyCollection())).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void insertsNewSystemsWithCodesAndAuditFields() throws Exception {
        ServiceResponse response = service.importSystem(excel(row("新增甲"), row("新增乙")));
        assertEquals(0, response.getCode());
        assertEquals("导入成功", response.getData());
        ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
        verify(persistence).saveBatch(captor.capture());
        List<DcInformationSystemManage> records = (List<DcInformationSystemManage>) captor.getValue();
        assertEquals(2, records.size());
        DcInformationSystemManage first = records.get(0);
        assertEquals("新增甲", first.getSystemName());
        assertNotNull(first.getId());
        assertNotEquals(first.getId(), records.get(1).getId());
        assertEquals("1", first.getStatus());
        assertEquals("1", first.getCreator());
        assertEquals("1", first.getUpdateUser());
        assertNotNull(first.getCreateTime());
        assertNotNull(first.getUpdateTime());
        assertEquals("2", first.getSystemState());
        assertEquals("1", first.getDeploymentMethod());
        assertEquals("1", first.getNetworkAccessType());
        assertEquals("-1", first.getCurrentAffiliatedUnitId());
        assertEquals("-1", first.getImplementationUnitId());
        assertEquals("测试功能", first.getFunctionDescription());
        verify(persistence, never()).updateBatchById(anyCollection());
    }

    @Test
    public void existingNameRejectsWholeFileEvenAfterValidRow() throws Exception {
        when(mapper.selectCount(any())).thenReturn(0, 1);
        ServiceResponse response = service.importSystem(excel(row("新增系统"), row("存量演示系统")));
        assertFailure(response, "信息系统名称已存在!导入只支持新增数据,不能覆盖或修改原有数据;");
        verifyNoInteractions(persistence);
        verify(mapper, never()).updateById(any());
    }

    @Test
    public void duplicateNamesInsideFileRejectWholeFile() throws Exception {
        ServiceResponse response = service.importSystem(excel(row("同名系统"), row("同名系统")));
        assertFailure(response, "信息系统名称在表格中重复!");
        verifyNoInteractions(persistence);
    }

    @Test
    public void missingRequiredValueRejectsWholeFile() throws Exception {
        DcInformationSystemManageImportVo invalid = row("不完整系统");
        invalid.setFunctionDescription(null);
        assertFailure(service.importSystem(excel(row("合法系统"), invalid)), "功能描述不能为空!");
        verifyNoInteractions(persistence);
    }

    @Test
    public void unknownOrganizationRejectsWholeFile() throws Exception {
        when(mapper.getOrgCount(anyString())).thenReturn(0);
        ServiceResponse response = service.importSystem(excel(row("新系统")));
        assertFailure(response, "当前所属单位不存在!");
        assertTrue(response.getDesc().contains("实施单位不存在!"));
        verifyNoInteractions(persistence);
    }

    @Test
    public void wrongHeaderIsRejected() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EasyExcel.write(output).head(Collections.singletonList(Collections.singletonList("错误表头")))
                .sheet().doWrite(Collections.singletonList(Collections.singletonList("新系统")));
        assertFailure(service.importSystem(file("test.xlsx", output.toByteArray())), "与模板信息系统名称不一致;");
        verifyNoInteractions(persistence);
    }

    @Test
    public void nonExcelExtensionIsRejected() throws Exception {
        assertFailure(service.importSystem(file("test.csv", new byte[0])), "文件格式有误!请重新导入!");
        verifyNoInteractions(mapper, persistence, dictionary);
    }

    @Test
    public void headerOnlyFileDoesNotWrite() throws Exception {
        assertEquals(0, service.importSystem(excel()).getCode());
        verifyNoInteractions(persistence);
    }

    private static void assertFailure(ServiceResponse response, String message) {
        assertEquals(-1, response.getCode());
        assertTrue(response.getDesc(), response.getDesc().contains(message));
    }

    private static DcInformationSystemManageImportVo row(String name) {
        DcInformationSystemManageImportVo row = new DcInformationSystemManageImportVo();
        row.setSystemName(name);
        row.setSystemState("已建");
        row.setDeploymentMethod("县区部署");
        row.setCurrentAffiliatedUnitId("广州市机构");
        row.setImplementationUnitId("广州市机构");
        row.setNetworkAccessType("政务内网");
        row.setFunctionDescription("测试功能");
        return row;
    }

    private static MultipartFile excel(DcInformationSystemManageImportVo... rows) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EasyExcel.write(output, DcInformationSystemManageImportVo.class).sheet().doWrite(Arrays.asList(rows));
        return file("test.xlsx", output.toByteArray());
    }

    private static MultipartFile file(String name, byte[] content) throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(name);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        return file;
    }
}
