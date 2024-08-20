[引言]
<!-- TOC -->
<#list ruleCodes as code>
* [【规则（${code}）】](#规则（${code}）)
</#list>

<!-- TOC -->


----------------

<#if indexs?? && (indexs?size > 0)>
<#list indexs as index>
# 【规则（${index.ruleCode}）】
${index.ruleName!""}

|  规则文件 | 业务结果 | 规则引擎结果 |
|------|------|------|
<#if index.results?? && (index.results?size > 0)>
<#list index.results as result>
| [${result.ruleFileName}](${result.ruleFileJsonPath}) | ${(result.bizTestResult == "成功" ||  result.bizTestResult == "失败") ? string("["+result.bizTestResult+"]("+result.bizFilePath+")",result.bizTestResult)} | ${(result.ruleTestResult == "成功" ||  result.ruleTestResult == "失败") ? string("["+result.ruleTestResult+"]("+result.ruleFilePath+")",result.ruleTestResult)} |
</#list>
</#if>

</#list>
</#if>