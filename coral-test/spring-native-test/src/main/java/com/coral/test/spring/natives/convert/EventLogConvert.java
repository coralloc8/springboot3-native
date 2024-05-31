package com.coral.test.spring.natives.convert;

import com.coral.test.spring.natives.dto.req.EventLogSaveDTO;
import com.coral.test.spring.natives.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.natives.dto.res.EventLogInfoDTO;
import com.coral.test.spring.natives.model.EventLog;
import com.coral.test.spring.natives.model.EventLogFullInfo;
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
