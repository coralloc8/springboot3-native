package com.coral.test.rule.dto;

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
public class PageInfoDTO<T> {

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

    private PageInfoDTO(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageInfoDTO<T> of(int pageNum, int pageSize) {
        return new PageInfoDTO<>(pageNum, pageSize);
    }

    public static <T> PageInfoDTO<T> of(PageQueryDTO pageQuery) {
        return new PageInfoDTO<>(pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    public PageInfoDTO<T> total(int total) {
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
        return this;
    }

    public PageInfoDTO<T> records(List<T> records) {
        this.records = records;
        return this;
    }

    public <R> PageInfoDTO<R> copy(List records) {
        return new PageInfoDTO<>(this.getPageNum(), this.getPageSize())
                .total(this.getTotal())
                .records(records);
    }
}
