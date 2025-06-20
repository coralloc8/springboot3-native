package com.coral.test.rule.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.coral.test.rule.config.RuleExecuteKey;
import com.coral.test.rule.config.RuleProperty;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.core.utils.StrFormatter;
import com.coral.test.rule.dto.RuleConfigInfoDTO;
import com.coral.test.rule.dto.RuleExecuteInfoDTO;
import com.coral.test.rule.dto.SqlInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 规则解析
 *
 * @author huss
 * @date 2024/7/3 16:36
 * @packageName com.coral.test.rule.service.impl
 * @className RuleParseHelper
 */
@Slf4j
public class RuleParseHelper {

    private static final RuleParseHelper HELPER = new RuleParseHelper();
    private final static String TABLE_SETTING_JSON = "{}_setting.json";
    private final static String TABLE_JSON = "{}.json";

    public static RuleParseHelper getInstance() {
        return HELPER;
    }


    /**
     * 解析文件前缀名 去掉.json 之类的后缀
     *
     * @param fileName
     * @return
     */
    public String parseFilePrefixName(String fileName) {
        return StringUtils.isBlank(fileName) ? "" : fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 解析文件名中的规则编码
     *
     * @param fileName
     * @return
     */
    public String parseFileRuleCode(String fileName) {
        return StringUtils.isBlank(fileName) ? "" : fileName.substring(0, fileName.indexOf("__"));
    }

    public String buildRuleResultHtmlPath(String fileName, String apiService) {
        return fileName + "_" + apiService + ".html";
    }

    public String buildRuleJsonHtmlPath(String fileName) {
        return fileName + "_json.html";
    }


    /**
     * 创建规则报表（html）
     *
     * @param markdown
     * @return
     */
    public String createRuleReportHtml(String markdown) {
        if (Objects.isNull(markdown)) {
            return "";
        }
        return MarkdownUtils.markdownToHtml(markdown);
    }

    /**
     * 查询文件
     *
     * @param basePath
     * @param ruleCodes
     * @return
     */
    public List<String> findFiles(String basePath, Set<String> ruleCodes) {
        List<String> fileNames = new ArrayList<>();
        try {
            fileNames = FileUtil.listFileNames(basePath);
        } catch (Exception e) {
            String absolutePathOfUserDir = getAbsolutePathByUserDir(basePath);
            log.error(">>>>> [findFiles] 查询文件失败,尝试以工作目录为起点重新查找，拼接的新目录为[{}].", absolutePathOfUserDir);
            try {
                fileNames = FileUtil.listFileNames(absolutePathOfUserDir);
            } catch (Exception e2) {
                log.error(">>>>> [findFiles] 以工作目录为起点查询文件失败", e2);
            }
        }
        if (CollUtil.isEmpty(fileNames)) {
            return Collections.emptyList();
        }
        Predicate<String> filter = t -> true;
        if (CollUtil.isNotEmpty(ruleCodes)) {
            filter = name -> ruleCodes.stream().anyMatch(name::startsWith);
        }
        return fileNames.stream().distinct()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 读取文件内容
     *
     * @param basePath
     * @param fileName
     * @return
     */
    public String readFileContent(String basePath, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            log.info(">>>>> [readFileContent] fileName为空.未能读取到文件内容.");
            return "";
        }
        String filePath = String.join("/", basePath, fileName);
        try {
            return String.join("\n", FileUtil.readLines(filePath, StandardCharsets.UTF_8));
        } catch (Exception e) {
            String absolutePathOfUserDir = getAbsolutePathByUserDir(basePath);
            filePath = String.join("/", absolutePathOfUserDir, fileName);
            log.error(">>>>> [readFileContent] 读取文件内容失败.尝试以工作目录为起点重新查找，拼接的新目录为[{}]", filePath);
            try {
                return String.join("\n", FileUtil.readLines(filePath, StandardCharsets.UTF_8));
            } catch (Exception e2) {
                log.error(">>>>> [readFileContent] 以工作目录为起点读取文件内容失败", e2);
                return "";
            }
        }
    }

    /**
     * 构建sql
     *
     * @param ruleConfigReq
     * @return
     */
    public List<SqlInfoDTO> buildSql(RuleConfigInfoDTO ruleConfigReq) {
        List<RuleConfigInfoDTO.Setting> settings = ruleConfigReq.getSettings();
        if (CollUtil.isEmpty(settings)) {
            log.info(">>>>>【规则配置解析】. 规则测试字段映射配置为空。构建SQL语句失败");
            return Collections.emptyList();
        }

        List<String> fileNames = RuleParseHelper.getInstance().findFiles(RuleProperty.RULE_CONFIG_PATH, null);
        if (CollUtil.isEmpty(fileNames)) {
            log.info("【规则配置解析】.查询到需要解析的规则文件列表为空.【流程终止】");
            return Collections.emptyList();
        }
        log.info("【规则配置解析】.查询到需要解析的规则文件列表为：{}", fileNames);

        List<SqlInfoDTO> sqlInfoDTOS = new ArrayList<>();

        String uniqueKey = ruleConfigReq.getUniqueKey();
        List<String> selectKeys = ruleConfigReq.getSelectKeys();
        for (RuleConfigInfoDTO.Setting setting : settings) {
            if (StringUtils.isBlank(setting.getUniqueKey())) {
                setting.setUniqueKey(uniqueKey);
            }
            if (CollUtil.isEmpty(setting.getSelectKeys())) {
                setting.setSelectKeys(selectKeys);
            }
            if (Objects.nonNull(setting.getFieldMapping())) {
                setting.setFieldMappings(Collections.singletonList(setting.getFieldMapping()));
            }
            if (CollUtil.isEmpty(setting.getFieldMappings())) {
                log.warn(">>>>>【规则配置解析】. [buildSql] table:[{}]没有字段映射配置,忽略进入下一轮循环.", setting.getTable());
                continue;
            }

            String tableJsonName = StrFormatter.format(TABLE_JSON, setting.getTable());
            if (!fileNames.contains(tableJsonName)) {
                continue;
            }
            String tableJson = readFileContent(RuleProperty.RULE_CONFIG_PATH, tableJsonName);
            if (StringUtils.isBlank(tableJson)) {
                log.warn(">>>>>【规则配置解析】. [buildSql] table:[{}]没有表对应配置,忽略进入下一轮循环.", setting.getTable());
                continue;
            }
            Map tableMap = JsonUtil.parseArray(tableJson, Map.class).get(0);

            // table setting
            String tableSettingJsonName = StrFormatter.format(TABLE_SETTING_JSON, setting.getTable());
            List<RuleConfigInfoDTO.Setting> tableSettings = new ArrayList<>();
            if (fileNames.contains(tableSettingJsonName)) {
                String tableSettingJson = readFileContent(RuleProperty.RULE_CONFIG_PATH, tableSettingJsonName);
                if (StringUtils.isNotBlank(tableSettingJson)) {
                    tableSettings = JsonUtil.parseArray(tableSettingJson, RuleConfigInfoDTO.Setting.class);
                }
            }

            List<SqlInfoDTO.TableSqlInfoDTO> tableSqlInfos = new ArrayList<>();

            for (Map<String, Object> fieldMapping : setting.getFieldMappings()) {
                // 创建查询语句
                String selectSql = createSelectSql(ruleConfigReq.getRequired(), ruleConfigReq.getOptional(), fieldMapping, setting);
                // 创建修改语句
                String updateSql = createUpdateSql(ruleConfigReq.getRequired(), ruleConfigReq.getOptional(), fieldMapping, setting, tableMap, tableSettings);
                // 创建插入语句
                String insertSql = createInsertSql(ruleConfigReq.getRequired(), ruleConfigReq.getOptional(), fieldMapping, setting, tableMap, tableSettings);
                //
                SqlInfoDTO.TableSqlInfoDTO tableSqlInfo = SqlInfoDTO.TableSqlInfoDTO.builder()
                        .select(selectSql)
                        .update(updateSql)
                        .insert(insertSql)
                        .build();
                tableSqlInfos.add(tableSqlInfo);
            }
            // 前置sql
            List<String> preSqls = CollUtil.isEmpty(setting.getPreSqls()) ? Collections.emptyList() :
                    setting.getPreSqls().stream()
                            .map(sql -> parseSql(sql, ruleConfigReq.getRequired(), ruleConfigReq.getOptional(), setting))
                            .collect(Collectors.toList());
            // 后置sql
            List<String> postSqls = CollUtil.isEmpty(setting.getPostSqls()) ? Collections.emptyList() :
                    setting.getPostSqls().stream()
                            .map(sql -> parseSql(sql, ruleConfigReq.getRequired(), ruleConfigReq.getOptional(), setting))
                            .collect(Collectors.toList());
            SqlInfoDTO sqlInfoDTO = SqlInfoDTO.builder()
                    .table(setting.getTable())
                    .sqlInfos(tableSqlInfos)
                    .preSqls(preSqls)
                    .postSqls(postSqls)
                    .build();

            sqlInfoDTOS.add(sqlInfoDTO);
        }
        return sqlInfoDTOS;
    }

    /**
     * 格式化值
     *
     * @param key
     * @param ruleConfigReq
     * @return
     */
    public Object formatValue(Object key, RuleConfigInfoDTO ruleConfigReq) {
        boolean isVariable = isVariable(key);
        if (!isVariable) {
            return key;
        }
        var variableName = parseVariableName(key);
        return buildFieldValue(variableName, ruleConfigReq.getRequired(), ruleConfigReq.getOptional(),
                null, null, null, false);
    }


    /**
     * 解析sql语句
     *
     * @param sql
     * @param required
     * @param optional
     * @param setting
     * @return
     */
    public String parseSql(String sql, Map<String, Object> required, Map<String, Object> optional, RuleConfigInfoDTO.Setting setting) {
        optional.put(RuleExecuteKey.TABLE_NAME, setting.getTable());
        String pattern = "\\$\\{.*?}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            String fieldKey = m.group().replaceAll("\\$\\{|}", "");
            boolean formatSql = !RuleExecuteKey.TABLE_NAME.equalsIgnoreCase(fieldKey);
            Object value = buildFieldValue(fieldKey, required, optional, null, null, null, formatSql);

            String originalKey = StrFormatter.format("\\$\\{{}}", fieldKey);
            sql = sql.replaceAll(originalKey, value.toString());
        }
        return sql;
    }

    /**
     * http异步发送
     *
     * @param ruleExecute
     * @return
     */
    public CompletableFuture<String> httpSendAsync(RuleExecuteInfoDTO ruleExecute, RuleConfigInfoDTO ruleConfigReq) {
        CompletableFuture<String> completableFuture = null;
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            String param = buildParam(ruleExecute.getData(), ruleExecute.getContentType(), ruleConfigReq);
            String url = ruleExecute.getUrl();
            HttpRequest.BodyPublisher bodyPublisher = null;
            if ("POST".equalsIgnoreCase(ruleExecute.getMethod()) || "PUT".equalsIgnoreCase(ruleExecute.getMethod())) {
                bodyPublisher = HttpRequest.BodyPublishers.ofString(param);
            } else {
                url += url.contains("?") ? "&" : "?";
                url += param;
            }
            log.info("【http异步发送】.接口地址:[{}], 参数:[{}].", url, param);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", ruleExecute.getContentType())
                    .timeout(Duration.ofSeconds(30));
            switch (ruleExecute.getMethod()) {
                case "GET" -> requestBuilder.GET();
                case "POST" -> requestBuilder.POST(bodyPublisher);
                case "PUT" -> requestBuilder.PUT(bodyPublisher);
                case "DELETE" -> requestBuilder.DELETE();
            }
            HttpRequest request = requestBuilder.build();
            completableFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .exceptionally(err -> {
                        log.error("请求异常:", err);
                        return "";
                    });
            completableFuture.join();
        } catch (Exception e) {
            log.error("请求异常:", e);
        }
        return completableFuture;
    }

    private String buildParam(Object data, String contentType, RuleConfigInfoDTO ruleTestReq) {
        if (Objects.isNull(data)) {
            return "";
        }
        boolean isMap = false;
        List<Map> list = new ArrayList<>();
        if (data instanceof Map map) {
            isMap = true;
            list.add(new HashMap(map));
        } else {
            list = new ArrayList<>((Collection<? extends Map>) data);
        }
        list.stream().forEach(e -> {
            e.forEach((k, v) -> {
                Object value = RuleParseHelper.getInstance().formatValue(v, ruleTestReq);
                e.put(k, value);
            });
        });
        String param = "";
        if (contentType.toUpperCase().contains("APPLICATION/JSON")) {
            param = isMap ? JsonUtil.toJson(list.get(0)) : JsonUtil.toJson(list);
        } else {
            param = list.stream().flatMap(e -> {
                Set<Map.Entry> entry = e.entrySet();
                return entry.stream().map(en -> StrFormatter.format("{}={}", en.getKey(), en.getValue()));
            }).collect(Collectors.joining("&"));
        }
        log.info(">>>>> [参数构建结果]: {}", param);
        return param;
    }

    /**
     * 创建查询语句
     *
     * @param required
     * @param optional
     * @param fieldMapping
     * @param setting
     * @return
     */
    private String createSelectSql(Map<String, Object> required, Map<String, Object> optional,
                                   Map<String, Object> fieldMapping, RuleConfigInfoDTO.Setting setting) {
        String sql = "SELECT * FROM {} where {};";
        String whereSql = buildWhereSql(required, optional, fieldMapping, setting);
        return StrFormatter.format(sql, setting.getTable(), whereSql);
    }


    /**
     * 创建插入语句
     *
     * @param required
     * @param optional
     * @param fieldMapping
     * @param setting
     * @param tableMap
     * @param tableSettings
     * @return
     */
    private String createInsertSql(Map<String, Object> required, Map<String, Object> optional,
                                   Map<String, Object> fieldMapping, RuleConfigInfoDTO.Setting setting,
                                   Map tableMap, List<RuleConfigInfoDTO.Setting> tableSettings) {
        String sql = "INSERT INTO {} ({}) VALUES ({});";
        List<String> fieldNames = new LinkedList<>();
        List<Object> fieldValues = new LinkedList<>();
        tableMap.forEach((k, v) -> {
            Object value = null;
            if (k.toString().equalsIgnoreCase(setting.getUniqueKey())) {
                value = RuleExecuteKey.getTargetValue(RuleExecuteKey.UUID, "");
            } else {
                value = (String) buildFieldValue(k.toString(), required, optional, fieldMapping, tableSettings, tableMap, true);
            }
            value = formatSqlValue(value, v);
            if (Objects.nonNull(value)) {
                fieldNames.add(k.toString());
                fieldValues.add(value);
            }
        });
        String field = String.join(", ", fieldNames);
        String value = fieldValues.stream().map(e -> {
            if (Objects.isNull(e)) {
                return "NULL";
            }
            return e.toString();
        }).collect(Collectors.joining(", "));
        return StrFormatter.format(sql, setting.getTable(), field, value);
    }

    /**
     * 创建修改语句
     *
     * @param required
     * @param optional
     * @param fieldMapping
     * @param setting
     * @param tableMap
     * @param tableSettings
     * @return
     */
    private String createUpdateSql(Map<String, Object> required, Map<String, Object> optional,
                                   Map<String, Object> fieldMapping, RuleConfigInfoDTO.Setting setting,
                                   Map tableMap, List<RuleConfigInfoDTO.Setting> tableSettings) {
        String sql = "UPDATE {} SET {} where {};";
        String whereSql = buildWhereSql(required, optional, fieldMapping, setting);
        List<String> updates = new LinkedList<>();
        tableMap.forEach((k, v) -> {
            String fieldName = k.toString();
            boolean canUpdate = fieldMapping.containsKey(fieldName);
            if (!canUpdate) {
                return;
            }

            Object fieldValue = formatSqlValue(fieldMapping.get(fieldName), v);

            String update = formatUpdateSetSql(fieldName, fieldValue);
            updates.add(update);

            tableSettings.stream().filter(tabSetting -> {
                String settingFieldValue = (String) fieldMapping.getOrDefault(tabSetting.getField(), "");
                return tabSetting.getField().equals(k) &&
                        settingFieldValue.equals(tabSetting.getFieldValue()) &&
                        Objects.nonNull(tabSetting.getFieldMapping());
            }).map(RuleConfigInfoDTO.Setting::getFieldMapping).forEach(map -> {
                map.forEach((sk, sv) -> {
                    Object curVal = formatSqlValue(String.valueOf(sv), sv);
                    String curUpdate = formatUpdateSetSql(sk, curVal);
                    updates.add(curUpdate);
                });
            });
        });
        String updateSql = String.join(", ", updates);
        return StrFormatter.format(sql, setting.getTable(), updateSql, whereSql);
    }


    /**
     * 构建where语句
     *
     * @param required
     * @param optional
     * @param fieldMapping
     * @param setting
     * @return
     */
    private String buildWhereSql(Map<String, Object> required, Map<String, Object> optional,
                                 Map<String, Object> fieldMapping, RuleConfigInfoDTO.Setting setting) {
        List<String> wheres = new LinkedList<>();
        String whereTemplate = "{} = {}";
        setting.getSelectKeys().forEach(select -> {
            String value = (String) buildFieldValue(select, required, optional, fieldMapping, null, null, true);
            if (StringUtils.isBlank(value)) {
                value = "''";
            }
            wheres.add(StrFormatter.format(whereTemplate, select, value));
        });
        return String.join(" AND ", wheres);
    }


    /**
     * 构建字段对应的值
     *
     * @param fieldKey
     * @param required
     * @param optional
     * @param fieldMapping
     * @param tableSettings
     * @param tableMap
     * @return
     */
    private Object buildFieldValue(String fieldKey, Map<String, Object> required, Map<String, Object> optional,
                                   Map<String, Object> fieldMapping, List<RuleConfigInfoDTO.Setting> tableSettings,
                                   Map tableMap, boolean formatForSql) {
        Object defValue = Objects.nonNull(tableMap) ? tableMap.get(fieldKey) : null;
        if (StringUtils.isBlank(fieldKey)) {
            return defValue;
        }

        Object value = null;
        if (Objects.nonNull(required)) {
            value = required.get(fieldKey);
        }
        if (Objects.isNull(value) && Objects.nonNull(optional)) {
            value = optional.get(fieldKey);
        }
        if (Objects.isNull(value) && Objects.nonNull(fieldMapping)) {
            value = fieldMapping.get(fieldKey);
        }
        if (Objects.isNull(value) && CollUtil.isNotEmpty(tableSettings)) {
            value = tableSettings.stream().filter(setting -> {
                String fieldValue = Objects.nonNull(fieldMapping) ? (String) fieldMapping.getOrDefault(setting.getField(), "") : "";
                if (StringUtils.isBlank(fieldValue) && Objects.nonNull(required)) {
                    fieldValue = (String) required.getOrDefault(setting.getField(), "");
                }
                if (StringUtils.isBlank(fieldValue) && Objects.nonNull(optional)) {
                    fieldValue = (String) optional.getOrDefault(setting.getField(), "");
                }
                return Objects.nonNull(setting.getFieldMapping()) &&
                        StringUtils.isNotBlank(fieldValue) &&
                        fieldValue.equals(setting.getFieldValue());
            }).map(setting -> setting.getFieldMapping().get(fieldKey)).filter(Objects::nonNull).findFirst().orElse(null);
        }
        // table的值优先度最低
        if (Objects.isNull(value) && Objects.nonNull(tableMap)) {
            value = tableMap.get(fieldKey);
        }

        // 没值最终查询是否是特定变量
        if (Objects.isNull(value)) {
            value = RuleExecuteKey.getTargetValue(fieldKey, defValue);
        }

        if (formatForSql) {
            return formatSqlValue(value, defValue);
        }
        return value;
    }


    private Object formatSqlValue(Object value, Object defValue) {
        if (Objects.isNull(value)) {
            return defValue;
        }
        String valueTemplate = "{}{}{}";
        String where = value.toString();
        boolean notNeedFormat = where.startsWith("'") && where.endsWith("'");
        if (!notNeedFormat && (value instanceof CharSequence)) {
            where = StrFormatter.format(valueTemplate, "'", value, "'");
        }
        return where;
    }

    private String formatUpdateSetSql(String fieldName, Object fieldValue) {
        String template = "{} = {}";
        String empty = "{} = NULL";
        if (Objects.isNull(fieldValue)) {
            return StrFormatter.format(empty, fieldName);
        }
        return StrFormatter.format(template, fieldName, fieldValue);
    }


    /**
     * 是否是变量
     *
     * @param key
     * @return
     */
    private boolean isVariable(Object key) {
        return Objects.nonNull(key) &&
                key instanceof String &&
                key.toString().startsWith("${") &&
                key.toString().endsWith("}");
    }

    /**
     * 提取变量名
     *
     * @param key
     * @return
     */
    private String parseVariableName(Object key) {
        String str = key.toString();
        return str.substring(2, str.length() - 1);
    }

    /**
     * 获取工作目录下的绝对路径
     *
     * @param basePath
     * @return
     */
    public String getAbsolutePathByUserDir(String basePath) {
        if (StringUtils.isBlank(basePath)) {
            return "";
        }
        String normalPath = FileUtil.normalize(basePath);
        if (FileUtil.isAbsolutePath(normalPath)) {
            // 给定的路径已经是绝对路径了
            return normalPath;
        }

        if (normalPath.startsWith(".")) {
            normalPath = normalPath.substring(1);
        }
        return Path.of(getUserDirPath(), normalPath).toString();
    }

    /**
     * 获取用户工作目录
     *
     * @return
     */
    private String getUserDirPath() {
        return System.getProperty("user.dir");
    }
}
