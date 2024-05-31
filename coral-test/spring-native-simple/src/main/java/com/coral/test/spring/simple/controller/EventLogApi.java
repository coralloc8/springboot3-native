package com.coral.test.spring.simple.controller;


import com.coral.test.spring.simple.config.ApiVersion;
import com.coral.test.spring.simple.core.response.IResult;
import com.coral.test.spring.simple.dto.PageInfo;
import com.coral.test.spring.simple.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogSaveDTO;
import com.coral.test.spring.simple.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.simple.dto.res.EventLogInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.coral.test.spring.simple.config.ApiVersion.SUCCESS_CODE;
import static com.coral.test.spring.simple.config.ApiVersion.SUCCESS_NAME;


/**
 * 事件日志
 *
 * @author huss
 * @date 2024/3/28 17:16
 * @packageName org.com.coral.test.spring.natives.controller
 * @className EventLogController
 */

@Tags
public interface EventLogApi {

    @Operation(summary = "【事件日志】根据事件ID查询事件日志信息", description = "【事件日志】根据事件ID查询事件日志信息", tags = {ApiVersion.API_1_0_0})
    @ApiResponses({
            @ApiResponse(
                    description = SUCCESS_NAME,
                    responseCode = SUCCESS_CODE,
                    content = @Content(
                            schema = @Schema(implementation = EventLogInfoDTO.class)
                    )
            )
    })
    @Parameters({
            @Parameter(name = "eventLogId", description = "事件ID"),
    })
    Mono<IResult<EventLogInfoDTO>> findEventLog(String eventLogId);

    @Operation(summary = "【事件日志】查询事件日志信息", description = "【事件日志】查询事件日志信息", tags = {ApiVersion.API_1_0_0})
    @ApiResponses({
            @ApiResponse(
                    description = SUCCESS_NAME,
                    responseCode = SUCCESS_CODE,
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EventLogInfoDTO.class)
                            )
                    )
            )
    })
    Mono<IResult<List<EventLogInfoDTO>>> findEventLogs(EventLogQueryDTO eventLogQuery);

    @Operation(summary = "【事件日志】查询事件日志完整信息", description = "【事件日志】查询事件日志完整信息", tags = {ApiVersion.API_1_0_0})
    @ApiResponses({
            @ApiResponse(
                    description = SUCCESS_NAME,
                    responseCode = SUCCESS_CODE,
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EventLogFullInfoDTO.class)
                            )
                    )
            )
    })
    Mono<IResult<List<EventLogFullInfoDTO>>> findEventFullLogs(EventLogQueryDTO eventLogQuery);

    @Operation(summary = "【事件日志】保存事件日志", description = "【事件日志】保存事件日志", tags = {ApiVersion.API_1_0_0})
    @ApiResponses({
            @ApiResponse(
                    description = SUCCESS_NAME,
                    responseCode = SUCCESS_CODE,
                    content = @Content(
                            schema = @Schema(implementation = EventLogInfoDTO.class)
                    )
            )
    })
    Mono<IResult<EventLogInfoDTO>> saveEventLog(EventLogSaveDTO eventLogSave);

    @Operation(summary = "【事件日志】分页查询事件日志", description = "【事件日志】分页查询事件日志", tags = {ApiVersion.API_1_0_0})
    @ApiResponses({
            @ApiResponse(
                    description = SUCCESS_NAME,
                    responseCode = SUCCESS_CODE,
                    content = @Content(
                            schema = @Schema(implementation = PageInfo.class)
                    )
            )
    })
    Mono<IResult<PageInfo<EventLogInfoDTO>>> findEventLogPages(EventLogPageQueryDTO eventLogPageQuery);

}
