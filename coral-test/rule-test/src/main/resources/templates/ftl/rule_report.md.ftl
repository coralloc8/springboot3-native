[引言]
<!-- TOC -->
* [【规则编码】](#规则编码)
* [【规则名称】](#规则名称)
* [【测试结果】](#测试结果)
* [【测试步骤说明】](#测试步骤说明)
* [【预期】](#预期)
    * [【预期结果】](#预期结果)
    * [【预期结果关键字】](#预期结果关键字)
    * [【预期结果细项】](#预期结果细项)
* [【结果集】](#结果集)
    * [【建议】](#建议)
    * [【建议项】](#建议项)
    * [【说明】](#说明)
* [【提示字段】](#提示字段)
<#if specialDesc>
* [【循证依据】](#循证依据)
    <#if itemEvidenceInfos?? && (itemEvidenceInfos?size > 0)>
    <#list itemEvidenceInfos as evi>
    * [【${evi.adviceItem}】](#${evi.adviceItem})
    </#list>
    </#if>

</#if>
<!-- TOC -->



# 【规则编码】
${ruleCode}

# 【规则名称】
${ruleName}

# 【测试结果】
${testResult ? string("成功","失败")}


# 【测试步骤说明】
<#if ruleDescs?? && (ruleDescs?size > 0)>
<#list ruleDescs as field>
* ${field}
</#list>
</#if>

------------------
# 【预期】
## 【预期结果】
${expectedResult!}

## 【预期结果关键字】
<#if expectedResultKeywords?? && (expectedResultKeywords?size > 0)>
<#list expectedResultKeywords as field>
* ${field}
</#list>
</#if>

## 【预期结果细项】
<#if expectedResultItems?? && (expectedResultItems?size > 0)>
<#list expectedResultItems as field>
* ${field}
</#list>
</#if>

-------------------
# 【结果集】
## 【建议】
${resultAdvice}
## 【建议项】
${resultAdviceItemText}
## 【说明】
${resultDesc}

-------------------
# 【提示字段】
<#if promptFields?? && (promptFields?size > 0)>
<#list promptFields as field>
* ${field}
</#list>
</#if>

-------------------
<#if specialDesc && itemEvidenceInfos?? && (itemEvidenceInfos?size > 0)>
# 【循证依据】
<#list itemEvidenceInfos as evi>
### 【${evi.adviceItem}】
| 模块名称 | 指标名称 | 提示标题 | 提示内容 |
|------|------|------|------|
<#if evi.evidenceDetailInfos?? && (evi.evidenceDetailInfos?size > 0)>
<#list evi.evidenceDetailInfos as detail>
| ${detail.moduleName}   | ${detail.indexName}   |  ${detail.tipTitle}   |  ${detail.tipContent}  |
</#list>
</#if>
</#list>
</#if>