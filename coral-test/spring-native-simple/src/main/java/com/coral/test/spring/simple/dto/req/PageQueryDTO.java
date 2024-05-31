package com.coral.test.spring.simple.dto.req;

import com.coral.test.spring.simple.core.enums.GlobalOrder;
import lombok.Data;

/**
 * 分页数据查询
 *
 * @author huss
 * @date 2024/4/3 17:18
 * @packageName com.coral.test.spring.natives.dto.req
 * @className PageQueryDTO
 */
@Data
public class PageQueryDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String sortField;
    private GlobalOrder order;
}
