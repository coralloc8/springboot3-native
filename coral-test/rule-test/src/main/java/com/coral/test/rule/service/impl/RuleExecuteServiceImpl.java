package com.coral.test.rule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.coral.test.rule.config.RuleProperty;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.dto.*;
import com.coral.test.rule.service.BizRuleApplyConfigQueryService;
import com.coral.test.rule.service.RuleExecuteService;
import com.coral.test.rule.util.FreeMarkerUtils;
import com.coral.test.rule.util.MarkdownUtils;
import com.coral.test.rule.util.RuleParseHelper;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 规则执行
 *
 * @author huss
 * @date 2024/7/8 16:54
 * @packageName com.coral.test.rule.service.impl
 * @className RuleExecuteServiceImpl
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class RuleExecuteServiceImpl implements RuleExecuteService {

    private static final String HTML_RULE_INDEX_PREFIX = "rule_index";

    private static final int API_TIMEOUT_SECOND = 30;

    private final R2dbcEntityTemplate template;

    private final BizRuleApplyConfigQueryService bizRuleApplyConfigQueryService;

    @Qualifier("taskExecutor")
    private final Executor taskExecutor;


    @Override
    public Mono<Void> execute() {
        Optional<Template> templateReportOpt = Optional.empty();
        Optional<Template> templateIndexOpt = Optional.empty();
        Optional<Template> templateJsonOpt = Optional.empty();
        try {
            // todo 打包成jar的情况下 目前在 流内部获取模板失败 需排查
            templateReportOpt = FreeMarkerUtils.getDefRuleReport();
            templateIndexOpt = FreeMarkerUtils.getDefRuleIndex();
            templateJsonOpt = FreeMarkerUtils.getDefRuleJson();
        } catch (Exception e) {
            log.error(">>>>> 获取 规则报告模板失败:", e);
        }
        Optional<Template> finalTemplateOptional = templateReportOpt;
        Optional<Template> finalTemplateIndexOpt = templateIndexOpt;
        Optional<Template> finalTemplateJsonOpt = templateJsonOpt;
        return Mono.fromFuture(CompletableFuture.runAsync(() -> {
            log.info("\n######################################## 【规则执行】.【执行开始】 ########################################\n");
            String basePath = RuleProperty.RULE_EXECUTE_PATH;
            List<String> fileNames = RuleParseHelper.getInstance().findFiles(basePath, null);
            if (CollUtil.isEmpty(fileNames)) {
                log.info("【规则执行】.未能查询到需要执行的规则文件.【流程终止】");
                return;
            }
            log.info("【规则执行】.查询到需要执行的规则文件列表为：{}", fileNames);

            List<RuleConfigInfoDTO> ruleConfigReqs = fileNames.stream()
                    .filter(fileName -> !RuleProperty.EXECUTE_CONFIG_NAME.equalsIgnoreCase(fileName))
                    .map(fileName -> {
                        String json = RuleParseHelper.getInstance().readFileContent(basePath, fileName);
                        // 写json文件
                        finalTemplateJsonOpt.ifPresent(temp -> {
                            writeJsonFile(RuleParseHelper.getInstance().parseFilePrefixName(fileName), json, temp);
                        });
                        RuleConfigInfoDTO ruleConfigInfo = StringUtils.isBlank(json) ? null : JsonUtil.parse(json, RuleConfigInfoDTO.class);
                        if (Objects.nonNull(ruleConfigInfo)) {
                            ruleConfigInfo.setFileName(fileName);
                        }
                        return ruleConfigInfo;
                    }).filter(Objects::nonNull).toList();

            //
            if (CollUtil.isEmpty(ruleConfigReqs)) {
                log.info("【规则执行】.解析规则文件结束,规则列表为空.【流程终止】");
                return;
            }

            // 需要执行的规则 编码集合
            Set<String> ruleFilePrefixNames = ruleConfigReqs.stream()
                    .map(e -> e.getFileName().substring(0, e.getFileName().lastIndexOf(".")))
                    .collect(Collectors.toSet());

            // 执行文件
            String json = RuleParseHelper.getInstance().readFileContent(basePath, RuleProperty.EXECUTE_CONFIG_NAME);
            if (StringUtils.isBlank(json)) {
                log.info("【规则执行】.解析规则执行文件为空.【流程终止】");
                return;
            }
            final List<RuleExecuteInfoDTO> ruleExecutes = JsonUtil.parseArray(json, RuleExecuteInfoDTO.class);

            Map<String, List<RuleConfigInfoDTO>> ruleConfigGroupMap = ruleConfigReqs.stream().collect(Collectors.groupingBy(RuleConfigInfoDTO::getDataUniqueKey));
            log.info("【规则执行】.根据数据唯一键分组后的key为: {}.", ruleConfigGroupMap.keySet());

            // 总数
            Map<String, Integer> totalMap = ruleConfigGroupMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().size()));
            // 计数器
            Map<String, AtomicLong> allCounter = ruleConfigGroupMap.keySet().stream()
                    .collect(Collectors.toMap(k -> k, v -> new AtomicLong(0)));

            List<RuleExecuteResponseInfoDTO> ruleResponses = new ArrayList<>();

            final Template template = finalTemplateOptional.orElse(null);
            ruleConfigGroupMap.entrySet().parallelStream().forEach(entry -> {
                List<RuleConfigInfoDTO> configs = entry.getValue();
                // 相同数据的情况下只能串行执行
                AtomicLong counter = allCounter.get(entry.getKey());
                configs.forEach(config -> {
                    // 数据准备
                    dataPreparation(config);
                    // 规则执行
                    executeOneRule(config, ruleExecutes, counter, template, ruleResponses);
                });
            });
            final Template templateIndex = finalTemplateIndexOpt.orElse(null);
            waitAllCompleted(totalMap, allCounter, ruleFilePrefixNames, ruleResponses, templateIndex);
        }));
    }

    /**
     * 规则异步执行结束
     *
     * @param totalMap
     * @param allCounter
     * @param ruleFilePrefixNames
     * @param ruleResponses
     * @param templateIndex
     */
    private void waitAllCompleted(Map<String, Integer> totalMap,
                                  Map<String, AtomicLong> allCounter,
                                  Set<String> ruleFilePrefixNames,
                                  List<RuleExecuteResponseInfoDTO> ruleResponses,
                                  Template templateIndex) {
        Supplier<Boolean> allCompleted = () -> totalMap.entrySet().stream()
                .allMatch(entry -> allCounter.get(entry.getKey()).get() == entry.getValue());
        if (waitCompleted(allCompleted, RuleProperty.EXECUTE_TIMOUT)) {
            writeIndexFile(ruleFilePrefixNames, ruleResponses, templateIndex);
            log.info("\n######################################## 【规则执行】.【执行结束】 ########################################\n");
        }
    }

    /**
     * 执行单条规则
     *
     * @param ruleConfig
     * @param ruleExecutes
     * @param counter
     * @param template
     * @param ruleResponses
     */
    private void executeOneRule(RuleConfigInfoDTO ruleConfig, List<RuleExecuteInfoDTO> ruleExecutes,
                                AtomicLong counter, Template template, List<RuleExecuteResponseInfoDTO> ruleResponses) {
        bizRuleApplyConfigQueryService.findEnabledRules()
//                .log("executeOneRule")
                .filter(bizRule -> bizRule.getRuleCodes().contains(ruleConfig.getRuleCode()))
                .map(bizRule -> ruleExecutes.stream()
                        .filter(ruleExecute -> bizRule.getTipsNode().equals(ruleExecute.getApiKey()))
                        .collect(Collectors.toList())
                )
                .subscribe(ruleExecs -> {
                            int count = ruleExecs.size();
                            if (CollUtil.isEmpty(ruleExecs)) {
                                log.info("【规则执行】.未能找到符合条件的api接口.fileName:[{}].ruleCode:[{}].【流程终止】", ruleConfig.getFileName(), ruleConfig.getRuleCode());
                                counter.incrementAndGet();
                                return;
                            }
                            AtomicInteger current = new AtomicInteger(0);
                            ruleExecs.forEach(ruleExecute -> {
                                // 规则执行
                                log.info("\n ##### 【规则执行】.规则执行中.fileName:[{}].ruleCode:[{}] #####", ruleConfig.getFileName(), ruleConfig.getRuleCode());
                                RuleParseHelper.getInstance().httpSendAsync(ruleExecute, ruleConfig).whenCompleteAsync((res, err) -> {
                                    if (Objects.nonNull(err)) {
                                        log.error("【规则执行】.执行异常.fileName:[{}].ruleCode:[{}].【流程终止】", ruleConfig.getFileName(), ruleConfig.getRuleCode(), err);
                                        current.incrementAndGet();
                                        return;
                                    }
                                    // todo 规则解析部分后续可以独立出来
                                    Optional<RuleExecuteResponseInfoDTO> responseOpt = RuleExecuteResponseInfoDTO.parse(res, ruleConfig, ruleExecute);
                                    if (responseOpt.isEmpty()) {
                                        log.info("【规则执行】.返回值为空.fileName:[{}].ruleCode:[{}].【流程终止】", ruleConfig.getFileName(), ruleConfig.getRuleCode());
                                        current.incrementAndGet();
                                        return;
                                    }
                                    RuleExecuteResponseInfoDTO responseInfo = responseOpt.get();
                                    // todo 文件写入后续可以独立出来
                                    writeFile(responseInfo, ruleConfig.getFileName(), ruleExecute.getApiService(), template);
                                    ruleResponses.add(responseInfo);
                                    log.info("【规则执行】.ruleCode:[{}].响应结果+1", ruleConfig.getRuleCode());
                                    current.incrementAndGet();
                                });
                            });
                            if (waitCompleted(() -> current.get() == count, API_TIMEOUT_SECOND)) {
                                log.info("【规则执行】.ruleCode:[{}].计数器+1.", ruleConfig.getRuleCode());
                                counter.incrementAndGet();
                            }
                        },
                        error -> log.error("【规则执行】.Error: " + error), // 错误时的回调
                        () -> log.info("【规则执行】.Completed.") // 完成时的回调
                );
    }


    /**
     * 数据准备
     *
     * @param ruleConfig
     */
    private void dataPreparation(RuleConfigInfoDTO ruleConfig) {
        List<SqlInfoDTO> sqlInfoDTOS = RuleParseHelper.getInstance().buildSql(ruleConfig);
        if (CollUtil.isEmpty(sqlInfoDTOS)) {
            return;
        }
        sqlInfoDTOS.forEach(sqlInfoDTO -> {
            //  前置sql执行
            sqlInfoDTO.getPreSqls().forEach(preSql -> {
                template.getDatabaseClient()
                        .sql(preSql)
                        .fetch().rowsUpdated()
                        .subscribe(res -> {
                            log.info("##### 【前置SQL执行情况】. ruleCode:[{}],table:[{}],SQL执行成功数量:[{}]. detail:[{}] #####",
                                    ruleConfig.getRuleCode(), sqlInfoDTO.getTable(), res, preSql);
                        });
            });

            // sql执行
            sqlInfoDTO.getSqlInfos().forEach(tableSqlInfo -> {
                template.getDatabaseClient()
                        .sql(tableSqlInfo.getSelect())
                        .fetch().first()
                        .log("rule-execute")
                        .flatMap(row -> template.getDatabaseClient().sql(tableSqlInfo.getUpdate()).fetch().rowsUpdated())
                        .switchIfEmpty(template.getDatabaseClient().sql(tableSqlInfo.getInsert()).fetch().rowsUpdated())
                        .onErrorResume(error -> {
                            log.error(">>>>> 【SQL执行异常】. ruleCode:[{}],table:[{}],ERROR: \n", ruleConfig.getRuleCode(), sqlInfoDTO.getTable(), error);
                            return Mono.just(0L);
                        })
                        .subscribe(res -> {
                            log.info("##### 【SQL执行情况】. ruleCode:[{}],table:[{}],SQL执行成功数量:[{}] #####",
                                    ruleConfig.getRuleCode(), sqlInfoDTO.getTable(), res);
                        });
            });

            // 后置sql执行
            sqlInfoDTO.getPostSqls().forEach(postSql -> {
                template.getDatabaseClient()
                        .sql(postSql)
                        .fetch().rowsUpdated()
                        .subscribe(res -> {
                            log.info("##### 【后置SQL执行情况】. ruleCode:[{}],table:[{}],SQL执行成功数量:[{}]. detail:[{}] #####",
                                    ruleConfig.getRuleCode(), sqlInfoDTO.getTable(), res, postSql);
                        });
            });
        });
    }

    /**
     * 文件写入  markdown文和html文件
     *
     * @param responseInfo
     * @param fileName
     * @param apiService
     * @param template
     */
    private void writeFile(RuleExecuteResponseInfoDTO responseInfo, String fileName, String apiService, Template template) {
        String basePath = RuleParseHelper.getInstance().getAbsolutePathByUserDir(RuleProperty.RULE_REPORT_PATH);

        String fileNameWithoutType = fileName.substring(0, fileName.lastIndexOf("."));
        String newFileName = String.join("_", fileNameWithoutType, apiService);

        final String markdownFileName = Path.of(basePath, "markdown", newFileName + ".md").toString();
        final String htmlFileName = Path.of(basePath, "html", newFileName + ".html").toString();
        log.info("【文件写入】. markdown 文件全路径: [{}]", markdownFileName);
        log.info("【文件写入】. html 文件全路径: [{}]", htmlFileName);
        try {
            String markdown = FreeMarkerUtils.create(template, responseInfo);
            if (StringUtils.isBlank(markdown)) {
                log.info("【文件写入】.创建markdown文件失败.规则:[{}].", responseInfo.getRuleCode());
                return;
            }
            FileUtil.writeString(markdown, markdownFileName, StandardCharsets.UTF_8);
            log.debug("【文件写入】.markdown文件内容为：\n {}", markdown);
            log.info("【文件写入】.创建markdown文件成功.规则:[{}].", responseInfo.getRuleCode());
            String html = MarkdownUtils.markdownToHtml(markdown);
            FileUtil.writeString(html, htmlFileName, StandardCharsets.UTF_8);
            log.debug("【文件写入】.html文件内容为：\n {}", html);
            log.info("【文件写入】.创建html文件成功.规则:[{}].", responseInfo.getRuleCode());
        } catch (Exception e) {
            log.error("【文件写入】异常.规则:[{}].", responseInfo.getRuleCode(), e);
        }
    }

    private void writeIndexFile(Set<String> ruleFilePrefixNames, List<RuleExecuteResponseInfoDTO> ruleResponses, Template templateIndex) {
        String basePath = RuleParseHelper.getInstance().getAbsolutePathByUserDir(RuleProperty.RULE_REPORT_PATH);
        final String markdownFileName = Path.of(basePath, "markdown", HTML_RULE_INDEX_PREFIX + ".md").toString();
        final String htmlFileName = Path.of(basePath, "html", HTML_RULE_INDEX_PREFIX + ".html").toString();
        log.info("【index文件写入】. markdown 文件全路径: [{}]", markdownFileName);
        log.info("【index文件写入】. html 文件全路径: [{}]", htmlFileName);
        try {
            List<RuleReportIndexInfoDTO> ruleReportIndexs = RuleReportIndexInfoDTO.create(ruleResponses, ruleFilePrefixNames);
            Set<String> ruleCodes = ruleFilePrefixNames.stream().map(e -> RuleParseHelper.getInstance().parseFileRuleCode(e)).collect(Collectors.toSet());
            log.debug(">>>>> 【index文件写入】 response json数据为:\n{}", JsonUtil.toJson(ruleResponses));
            log.debug(">>>>> 【index文件写入】 json数据为:\n{}", JsonUtil.toJson(ruleReportIndexs));
            Map<String, Object> map = new HashMap<>();
            map.put("ruleCodes", ruleCodes);
            map.put("indexs", ruleReportIndexs);
            String markdown = FreeMarkerUtils.create(templateIndex, map);
            if (StringUtils.isBlank(markdown)) {
                log.info("【index文件写入】.创建markdown文件失败.");
                return;
            }
            FileUtil.writeString(markdown, markdownFileName, StandardCharsets.UTF_8);
            log.debug("【index文件写入】.markdown文件内容为：\n {}", markdown);
            log.info("【index文件写入】.创建markdown文件成功.");
            String html = MarkdownUtils.markdownToHtml(markdown);
            FileUtil.writeString(html, htmlFileName, StandardCharsets.UTF_8);
            log.debug("【index文件写入】.html文件内容为：\n {}", html);
            log.info("【index文件写入】.创建html文件成功.");
        } catch (Exception e) {
            log.error("【index文件写入】异常.", e);
        }
    }

    private void writeJsonFile(String filePrefixName, String json, Template templateJson) {
        try {
            String basePath = RuleParseHelper.getInstance().getAbsolutePathByUserDir(RuleProperty.RULE_REPORT_PATH);
            Map<String, Object> map = new HashMap<>();
            map.put("json", json);
            String markdown = FreeMarkerUtils.create(templateJson, map);
            if (StringUtils.isBlank(markdown)) {
                log.info("【json文件写入】.创建markdown文件失败.");
                return;
            }
            String jsonName = filePrefixName + "_json";
            final String markdownFileName = Path.of(basePath, "markdown", jsonName + ".md").toString();
            final String htmlFileName = Path.of(basePath, "html", jsonName + ".html").toString();
            log.info("【json文件写入】. markdown 文件全路径: [{}]", markdownFileName);
            log.info("【json文件写入】. html 文件全路径: [{}]", htmlFileName);
            FileUtil.writeString(markdown, markdownFileName, StandardCharsets.UTF_8);
            log.debug("【json文件写入】.markdown文件内容为：\n {}", markdown);
            log.info("【json文件写入】.创建markdown文件成功.");
            String html = MarkdownUtils.markdownToHtml(markdown);
            FileUtil.writeString(html, htmlFileName, StandardCharsets.UTF_8);
            log.debug("【json文件写入】.html文件内容为：\n {}", html);
            log.info("【json文件写入】.创建html文件成功.");
        } catch (Exception e) {
            log.error("【json文件写入】异常.", e);
        }
    }

    private boolean waitCompleted(Supplier<Boolean> supplier, long timeoutOfSecond) {
        long period = 1; //秒
        final long maxExecutions = timeoutOfSecond / period; // 设置最大执行次数
        final AtomicInteger executionCount = new AtomicInteger(0);

        Timer timer = new Timer();
        try {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean finished = false;
                    int currentExecution = executionCount.incrementAndGet();
                    if (currentExecution > maxExecutions) {
                        // 关闭executorService
                        log.info("【waitCompleted】任务执行次数已达到最大限制，结束本次任务。");
                        finished = true;
                    }
                    if (supplier.get()) {
                        log.info("【waitCompleted】任务执行完成，结束本次任务。");
                        finished = true;
                    }
                    if (finished) {
                        future.complete(true);
                        cancel();
                    }
                }
            }, 0, period * 1000);
            return future.get(timeoutOfSecond, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("【waitCompleted】 Error.", e);
        }
        return false;
    }
}
