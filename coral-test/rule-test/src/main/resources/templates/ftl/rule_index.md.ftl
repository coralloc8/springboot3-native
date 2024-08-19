[引言]
<!-- TOC -->
<#list ruleCodes as code>
* [【规则（${code}）】](#规则（${code}）)
</#list>

<!-- TOC -->


<#list indexs as index>
# 【规则（${index.ruleCode}）】
${index.ruleName}

|规则文件|业务结果|规则引擎结果|
|-|-|-|
<#list index.results as result>
|${result.ruleFileName}|${(result.bizTestResult == "成功" ||  result.bizTestResult == "失败") ? string("["+result.bizTestResult+"]("+result.bizFilePath+")",result.bizTestResult)}|${(result.ruleTestResult == "成功" ||  result.ruleTestResult == "失败") ? string("["+result.ruleTestResult+"]("+result.ruleFilePath+")",result.ruleTestResult)}|
</#list>


</#list>