package com.coral.test.spring.simple.dto;

import com.coral.test.spring.simple.dto.req.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

/**
 * 分页信息
 *
 * @author huss
 * @date 2024/4/3 17:27
 * @packageName com.coral.test.spring.natives.dto
 * @className PageInfoDTO
 */
@Schema(description = "分页信息")
@Data
@Setter(AccessLevel.PRIVATE)
public class PageInfo<T> {

    @Schema(description = "当前页")
    private int pageNum;

    @Schema(description = "每页条数")
    private int pageSize;

    @Schema(description = "总条数")
    private int total;

    @Schema(description = "返回的数据")
    private List<T> records;

    @Schema(description = "总页数")
    private int pages;

    private PageInfo(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageInfo<T> of(int pageNum, int pageSize) {
        return new PageInfo<>(pageNum, pageSize);
    }

    public static <T> PageInfo<T> of(PageQueryDTO pageQuery) {
        return new PageInfo<>(pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    public PageInfo<T> total(int total) {
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
        return this;
    }

    public PageInfo<T> records(List<T> records) {
        this.records = records;
        return this;
    }

    public <R> PageInfo<R> copy(List records) {
        return new PageInfo<>(this.getPageNum(), this.getPageSize())
                .total(this.getTotal())
                .records(records);
    }
}
