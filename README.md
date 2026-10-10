# import-demo：业务系统管理 Excel 导入接口最小演示项目

从公司项目 `dataCenter_backend` 的 `dataCenter-center/system/manage/importSystem` 接口提炼出的
**最小可运行模仿项目**。保留原项目的分层结构与核心代码写法（Controller → Service → EasyExcel 监听器 →
Mapper），数据库按需求换成 MySQL，用户信息按需求硬编码，其余依赖全部最小化。

## 与原项目的对照

| 项 | 原项目（dataCenter_backend） | 本项目 |
|---|---|---|
| 数据库 | GaussDB（gsjdbc200 驱动，`"name"` 双引号标识符） | MySQL 8（mysql-connector 8.0.25，SQL 改为反引号/裸列名） |
| 字典转换 | Redis 缓存 `yqfk_sysParam_*`，只查缓存**不回源** | 直接查 `sys_param` 表（等价于原项目缺失的"回源"兜底） |
| 当前用户 | Spring Security OAuth 登录态 + UserInfoUtils | 硬编码 `DEFAULT_USER_ID = "1"`（Listener 内常量） |
| 接口文档 | springfox swagger 注解 | 已去掉（减少依赖），接口见下文 |
| 分页列表 | PageHelper + 条件查询 VO | 简化 GET 全量列表（仅验收用，非移植范围） |
| 依赖 | Spring Cloud Alibaba / Nacos / Druid / Redisson / fastjson 等 | 仅 web + mybatis-plus 3.4.1 + easyexcel 3.3.2 + mysql 驱动 + lombok |
| 版本 | Spring Boot 2.3.12 / MP 3.4.1 / EasyExcel 3.3.2 | 完全一致 |

`DcInformationSystemManageImportListener` 是核心，**逻辑与原项目逐行对应**，
包括原项目的 upsert 修复（`existingIdMap` + `batchNames` + insert/update 分流）。

## 目录结构

```
import/
├── pom.xml
├── sql/init.sql                         # 建库建表 + 字典/单位/存量演示数据
├── docs/
│   ├── 信息系统管理导入模版.xlsx          # 空模板（表头与 @ExcelProperty 严格一致）
│   └── 示例导入数据.xlsx                 # 两行示例：覆盖更新 + 新增
└── src/main/
    ├── java/com/esgov/yqfk/
    │   ├── ImportApplication.java
    │   ├── controller/DcInformationSystemManageController.java
    │   ├── core/dto/ServiceResponse.java          # 统一返回（精简自原 common）
    │   ├── core/dto/enums/ResponseCode.java
    │   ├── entity/BaseEntity.java                 # 审计字段基类
    │   ├── entity/dc/DcInformationSystemManage.java
    │   ├── vo/dc/importVo/DcInformationSystemManageImportVo.java
    │   ├── listener/DcInformationSystemManageImportListener.java   # ★核心
    │   ├── mapper/{SysParamMapper.java, dc/DcInformationSystemManageMapper.java}
    │   ├── service/{ISysParamService, IDcInformationSystemManageService}
    │   ├── service/impl/{SysParamServiceImpl, DcInformationSystemManageServiceImpl}
    │   └── util/{SpringContextUtil, UUIDUtil}
    └── resources/
        ├── application.yml               # 端口 9802，MySQL 连接（改密码）
        └── mapper/**/*.xml
```

## 快速开始

1. **建库建表**：在装有 MySQL 的机器上执行 `sql/init.sql`（已含种子数据）。
2. **改连接**：`src/main/resources/application.yml` 里改数据库地址与 `username/password`。
3. **构建**（本机验证通过，2026-10-10）：

   ```
   # 首次：用项目内临时 settings 直达公共 central 拉依赖（用户全局 settings 的 mirror 指向公司仓库，本机不可达）
   mvn -s tmp-mvn-settings.xml clean package -DskipTests
   # 依赖缓存后也可离线构建
   mvn -o clean package -DskipTests
   ```

   产物 `target/import-demo-1.0-SNAPSHOT.jar`（可执行 fat jar）。
4. **启动**：`java -jar target/import-demo-1.0-SNAPSHOT.jar`，或 IDEA 直接 Run `ImportApplication`。
   启动后 `http://127.0.0.1:9802`。
4. **导入测试**（Postman / curl）：

   ```
   POST http://127.0.0.1:9802/system/manage/importSystem
   form-data: file=<选择 docs/示例导入数据.xlsx>
   ```

   - 第 1 行"存量演示系统"已存在于种子数据 → **覆盖更新**：主键不变、
     创建人/创建时间保留、六个业务字段刷新、`update_time` 更新；
   - 第 2 行"新增演示系统"不存在 → **新增**：新 UUID、`status=1`。
5. **看结果**：`GET http://127.0.0.1:9802/system/manage/getSystemManageList`（控制台会打印 SQL）。

## 导入规则（与原项目一致）

- **文件格式**：仅 `.xlsx`/`.xls`（扩展名精确匹配，大小写敏感）。
- **表头校验**：前 7 列必须与模板完全一致（反射读 `@ExcelProperty` 动态比对），不一致整批报错。
- **必填校验**：7 列均必填，错误信息带 Excel 行号（如"第2行功能描述不能为空!"）。
- **查重**：按 `system_name` + `status='1'` 查库，已存在 → 更新；不存在但在本表格内出现两次 → 整批失败。
- **单位**：填单位**中文名**（如"广州市机构"），落库前转单位 id；填了不存在的名字 → 报错并中止。
- **字典**：状态/部署方式/接入网络类型填**中文值**（如"已建""县区部署""政务内网"），落库前转字典码。
- 任一行校验失败（EasyExcel 默认 throw）→ **整批不落库**，返回错误信息。

## 从原项目继承的已知缺陷（简历面试素材）

1. 落库循环里 `catch(Exception){ e.printStackTrace(); }` 会**静默跳过单行**，接口仍返回"导入成功"；
2. `saveBatch`/`updateBatchById` 之间**无事务**，insert 成功 + update 失败时数据不一致（监听器 new 出来，`@Transactional` 也不生效）；
3. 异常信息 `e.getMessage()` 直接透传给前端；
4. 逐行 3 次 SQL 查库（查重 + 两个单位查询），无预加载；
5. 应用层查重，无数据库唯一约束，并发下可插入同名数据。

这些是有意保留的"原貌"，便于对照讲解与提出优化方向（失败行回执、事务化、批量预加载、唯一索引兜底等）。

## 备注

- 表结构来自 GaussDB 真实库探针采集（2026-10-10），列名/长度/注释/索引与原表一致；
  `sys_org` 按导入依赖裁剪为 3 列（原表 29 列）。
- 原项目 GaussDB 写法 `WHERE "name"=#{name}`（双引号标识符）在 MySQL 会变成字符串字面量
  导致查询永远为空，移植时已改为裸列名，`DcInformationSystemManageMapper.xml` 内有注释说明。
- 构建相关：原项目以 `spring-boot-starter-parent` 为 parent，本机离线仓库缺该 parent POM，
  故改用 `spring-boot-dependencies` 2.3.12 BOM import + 显式插件版本（compiler 3.1 与原项目
  data-center 一致）；`tmp-mvn-settings.xml` 为项目内临时 settings，仅绕过本机全局配置中
  指向不可达公司仓库的 mirror，不影响全局配置。
