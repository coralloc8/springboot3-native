package com.coral.test.spring.simple.repository;

import com.coral.test.spring.simple.core.r2dbc.SQLFactory;
import com.coral.test.spring.simple.core.utils.StrFormatter;
import com.coral.test.spring.simple.dto.PageInfo;
import com.coral.test.spring.simple.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogQueryDTO;
import com.coral.test.spring.simple.model.EventInfo;
import com.coral.test.spring.simple.model.EventLog;
import com.coral.test.spring.simple.model.EventLogFullInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件日志
 *
 * @author huss
 * @date 2024/4/1 13:53
 * @packageName com.coral.test.spring.natives.repository
 * @className EventLogRepositoryFactory
 */
@Slf4j
class EventLogCustomRepositoryImpl implements EventLogCustomRepository {
    private final static Class<EventLog> ENTITY_CLASS = EventLog.class;
    @Resource
    private R2dbcEntityTemplate template;

    @Override
    public Flux<EventLog> findEventLogs(@NonNull EventLogQueryDTO eventLogQuery) {
        List<Criteria> wheres = buildWheres(eventLogQuery, "");
        Query query = SQLFactory.query(Criteria.from(wheres));
        return template.select(query, ENTITY_CLASS);
    }


    /**
     * 分页查询事件日志
     *
     * @param eventLogPageQuery 分页查询入参
     * @return PageInfo<EventLog>
     */
    @Override
    public Mono<PageInfo<EventLog>> findEventLogPages(EventLogPageQueryDTO eventLogPageQuery) {
        List<Criteria> wheres = buildWheres(eventLogPageQuery, "");
        var page = SQLFactory.pageOf(eventLogPageQuery.getPage());
        var query = SQLFactory.query(Criteria.from(wheres)).with(page);
        PageInfo<EventLog> pageInfo = PageInfo.of(eventLogPageQuery.getPage());
        return SQLFactory.pageQuery(template, query, ENTITY_CLASS, pageInfo);
    }

    /**
     * 查询事件日志完整信息
     *
     * @param eventLogQuery
     * @return
     */
    @Override
    public Flux<EventLogFullInfo> findEventFullLogs(EventLogQueryDTO eventLogQuery) {
        String sql = """
                SELECT
                    el.*,
                    ei.event_source,
                    ei.event_name,
                    ei.event_key
                FROM
                    `event_log` el
                    INNER JOIN `event_info` ei ON el.event_id = ei.event_id
                """;
        List<Criteria> criteria = buildWheres(eventLogQuery, "el");
        criteria.addAll(buildEventInfoWheres(eventLogQuery, "ei"));
        Criteria where = Criteria.from(criteria);
        var fullSql = SQLFactory.buildFullSql(sql, where);

        return SQLFactory.select(template, fullSql, EventLogFullInfo.class);
    }


    private List<Criteria> buildWheres(EventLogQueryDTO eventLogQuery, String prefix) {
        List<Criteria> wheres = new ArrayList<>(32);
        SQLFactory.create(SQLFactory.formatFieldName(EventLog::getEventLogId, prefix),
                eventLogQuery.getEventLogId()).ifPresent(wheres::add);
        SQLFactory.create(SQLFactory.formatFieldName(EventLog::getProject, prefix),
                eventLogQuery.getProject()).ifPresent(wheres::add);
        SQLFactory.create(SQLFactory.formatFieldName(EventLog::getFuncCode, prefix),
                eventLogQuery.getFuncCodes()).ifPresent(wheres::add);
//        SQLFactory.create(SQLFactory.formatFieldName(EventLog::getStatus, prefix),
//                eventLogQuery.getStatus()).ifPresent(wheres::add);
        return wheres;
    }

    private List<Criteria> buildEventInfoWheres(EventLogQueryDTO eventLogQuery, String prefix) {
        List<Criteria> wheres = new ArrayList<>(32);
        if (StringUtils.isNotBlank(eventLogQuery.getEventName())) {
            wheres.add(SQLFactory.step(SQLFactory.formatFieldName(EventInfo::getEventName, prefix))
                    .like(StrFormatter.format("%{}%", eventLogQuery.getEventName()))
            );
        }
        if (StringUtils.isNotBlank(eventLogQuery.getEventSource())) {
            wheres.add(SQLFactory.step(SQLFactory.formatFieldName(EventInfo::getEventSource, prefix))
                    .like(StrFormatter.format("%{}%", eventLogQuery.getEventSource()))
            );
        }
        SQLFactory.create(SQLFactory.formatFieldName(EventInfo::getEventKey, prefix),
                eventLogQuery.getEventKey()).ifPresent(wheres::add);
        return wheres;
    }
}
