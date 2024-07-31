package com.coral.test.rule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * sql语句信息 支持持单表
 *
 * @author huss
 * @date 2024/7/3 16:55
 * @packageName com.coral.test.rule.dto
 * @className SqlInfo
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SqlInfoDTO {

    /**
     * 表名
     */
    private String table;

    /**
     * 前置sql
     */
    private List<String> preSqls;

    /**
     * 后置sql
     */
    private List<String> postSqls;

    /**
     * 表sql语句
     */
    private List<TableSqlInfoDTO> sqlInfos;


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class TableSqlInfoDTO {

        /**
         * 查询语句
         */
        private String select;

        /**
         * 修改语句
         */
        private String update;

        /**
         * 插入语句
         */
        private String insert;
    }
}
