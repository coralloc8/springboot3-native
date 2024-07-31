package com.coral.test.rule.controller;

import com.coral.test.rule.config.ApiVersion;
import com.coral.test.rule.core.response.IResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import reactor.core.publisher.Mono;

import static com.coral.test.rule.config.ApiVersion.SUCCESS_CODE;
import static com.coral.test.rule.config.ApiVersion.SUCCESS_NAME;

/**
 * 事件日志
 *
 * @author huss
 * @date 2024/3/28 17:16
 * @packageName org.com.coral.test.spring.natives.controller
 * @className EventLogController
 */

@Tags
public interface RuleTestApi {

//    @Operation(summary = "【规则】执行规则", description = "【规则】执行规则", tags = {ApiVersion.API_1_0_0})
//    @ApiResponses({
//            @ApiResponse(
//                    description = SUCCESS_NAME,
//                    responseCode = SUCCESS_CODE,
//                    content = @Content(
//                            schema = @Schema(implementation = EventLogInfoDTO.class)
//                    )
//            )
//    })
//    Mono<IResult<EventLogInfoDTO>> saveEventLog(EventLogSaveDTO eventLogSave);


}
