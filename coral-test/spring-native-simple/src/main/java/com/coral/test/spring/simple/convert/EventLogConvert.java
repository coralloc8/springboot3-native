package com.coral.test.spring.simple.convert;

import com.coral.test.spring.simple.dto.req.EventLogSaveDTO;
import com.coral.test.spring.simple.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.simple.dto.res.EventLogInfoDTO;
import com.coral.test.spring.simple.model.EventLog;
import com.coral.test.spring.simple.model.EventLogFullInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 事件日志转换
 *
 * @author huss
 * @date 2024/4/15 16:10
 * @packageName com.coral.test.spring.simple.convert
 * @className EventLogConvert
 */
@Mapper
public interface EventLogConvert {
    EventLogConvert INSTANCE = Mappers.getMapper(EventLogConvert.class);

    EventLogInfoDTO convertEventLogInfoDTO(EventLog eventLog);

    EventLogFullInfoDTO convertEventLogFullInfoDTO(EventLogFullInfo eventLogFullInfo);

    EventLog convertEventLog(EventLogSaveDTO eventLogSave);
}
