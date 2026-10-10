package com.esgov.yqfk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述: 导入演示项目启动类
 */
@SpringBootApplication
@MapperScan("com.esgov.yqfk.mapper")
public class ImportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportApplication.class, args);
    }
}
