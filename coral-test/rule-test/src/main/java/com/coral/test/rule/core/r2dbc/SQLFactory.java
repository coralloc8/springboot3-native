package com.coral.test.rule.core.r2dbc;

import com.coral.test.rule.config.R2dbcConfig;
import com.coral.test.rule.core.enums.GlobalOrder;
import com.coral.test.rule.core.utils.NameStyleUtil;
import com.coral.test.rule.core.utils.StrFormatter;
import com.coral.test.rule.core.utils.reflection.IGetter;
import com.coral.test.rule.core.utils.reflection.LambdaFieldUtil;
import com.coral.test.rule.dto.PageInfoDTO;
import com.coral.test.rule.dto.PageQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询条件组装
 *
 * @author huss
 * @date 2024/4/2 17:15
 * @packageName com.coral.test.rule.core.r2dbc
 * @className SQLFactory
 */
@Slf4j
public class SQLFactory {

    // 符号引用
    private final static String QUOTATION_MARKS = R2dbcConfig.QUOTATION_MARKS;

    /**
     * 构建完整sql语句
     *
     * @param sql
     * @param whereCriteria
     * @return
     */
    public static String buildFullSql(String sql, Criteria whereCriteria) {
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        sql = formatSql(sql);
        if (Objects.isNull(whereCriteria) || whereCriteria.isEmpty()) {
            return sql;
        }
        String where = whereCriteria.toString();
        if (sql.contains("where") || sql.contains("WHERE")) {
            return sql + " AND " + where;
        }
        return sql + " WHERE " + where;

    }

    /**
     * 格式化查询条件字段
     *
     * @param getter
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> String formatFieldName(IGetter<T> getter, String prefix) {
        return formatFieldName(LambdaFieldUtil.getFieldName(getter), prefix);
    }

    /**
     * 格式化查询条件字段
     *
     * @param fieldName
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> String formatFieldName(String fieldName, String prefix) {
        var quotationMarks = QUOTATION_MARKS;
        fieldName = NameStyleUtil.humpToLine(fieldName);
        if (StringUtils.isNotBlank(prefix)) {
            return StrFormatter.format("{}{}{}.{}{}{}", quotationMarks, prefix, quotationMarks, quotationMarks, fieldName, quotationMarks);
        }
        return StrFormatter.format("{}{}{}", quotationMarks, fieldName, quotationMarks);
    }

    /**
     * 查询数据
     *
     * @param template
     * @param sql
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> Flux<T> select(R2dbcEntityTemplate template, String sql, Class<T> entityClass) {
        return template.getDatabaseClient().sql(sql)
                .map(new EntityRowMapper<>(entityClass, template.getConverter()))
                .all();
    }

    /**
     * 分页数据查询
     *
     * @param entityClass
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Mono<PageInfoDTO<T>> pageQuery(R2dbcEntityTemplate template, Query query, Class<R> entityClass, PageInfoDTO<T> pageInfoDTO) {
        return template.count(query, entityClass)
                .flatMap(total -> template.select(query, entityClass)
                        .collectList()
                        .map(list -> pageInfoDTO.total(Math.toIntExact(total)).records((List<T>) list))
                );
    }

    public static Pageable pageOf(PageQueryDTO page) {
        int pageNum = 0;
        int pageSize = 20;
        if (Objects.isNull(page)) {
            return PageRequest.of(pageNum, pageSize);
        }
        pageNum = Objects.nonNull(page.getPageNum()) && page.getPageNum() > 0 ? page.getPageNum() - 1 : pageNum;
        pageSize = Objects.nonNull(page.getPageSize()) && page.getPageSize() > 0 ? page.getPageSize() : pageSize;

        if (StringUtils.isNotBlank(page.getSortField())) {
            Sort.Direction direction = Objects.isNull(page.getOrder()) || GlobalOrder.DESC.equals(page.getOrder()) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, page.getSortField());
            return PageRequest.of(pageNum, pageSize, sort);
        }
        return PageRequest.of(pageNum, pageSize);
    }

    public static Query query(Criteria criteria) {
        Query query = Query.query(criteria);
        query.getCriteria().ifPresent(c -> log.info("SQL查询条件为: {}", c));
        return query;
    }

    public static Criteria init() {
        return Criteria.empty();
    }

    public static List<String> columns(IGetter<?>... getters) {
        return Objects.nonNull(getters) ?
                Arrays.stream(getters).map(LambdaFieldUtil::getFieldName).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public static Criteria.CriteriaStep step(@NonNull String field) {
        return Criteria.where(field);
    }

    public static <T> Criteria.CriteriaStep step(@NonNull IGetter<T> getter) {
        return Criteria.where(LambdaFieldUtil.getFieldName(getter));
    }

    public static <T> Optional<Criteria> create(@NonNull IGetter<T> getter, @Nullable Object value) {
        return create(LambdaFieldUtil.getFieldName(getter), value);
    }

    public static Optional<Criteria> create(@NonNull String field, @Nullable Object value) {
        boolean collEmpty = value instanceof Collection<?> collection && collection.isEmpty();
        boolean strEmpty = value instanceof String str && StringUtils.isBlank(str);
        if (Objects.isNull(value) || collEmpty || strEmpty) {
            return Optional.empty();
        }
        Criteria current;
        Criteria.CriteriaStep step = Criteria.where(field);
        if (value instanceof Collection<?> collection) {
            var perVal = collection.iterator().next();
            if (perVal instanceof IMapping<?>) {
                current = step.in(collection.stream().map(e -> ((IMapping) e).getValue()).collect(Collectors.toList()));
            } else {
                current = step.in(collection);
            }
        } else if (value instanceof IMapping<?> iMapping) {
            current = step.is(iMapping.getValue());
        } else {
            current = step.is(value);
        }
        return Optional.of(current);
    }


    /**
     * 格式化sql语句
     *
     * @param sql
     * @return
     */
    private static String formatSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        return sql.replaceAll("`", QUOTATION_MARKS).replaceAll("\"", QUOTATION_MARKS);
    }


}
