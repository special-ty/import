package com.esgov.yqfk.util;

import java.util.UUID;

/**
 * 描述: 精简自原项目 common 模块的 UUIDUtil，仅保留 32 位 UUID 生成
 *
 * @author Yangjinming
 * @create 2018-03-28 上午11:17
 */
public class UUIDUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
