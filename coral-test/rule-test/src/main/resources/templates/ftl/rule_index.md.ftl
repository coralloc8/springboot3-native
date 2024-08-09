[引言]
<!-- TOC -->
* [【规则名】](#规则名)
* [【说明】](#规则说明)
* [【类型】](#类型)
* [【结果集】](#结果集)
    * [【建议】](#建议)
    * [【建议项】](#建议项)
    * [【说明】](#说明)
* [【提示字段】](#提示字段)
<#if specialDesc>
* [【循证依据】](#循证依据)
    <#list itemEvidenceInfos as evi>
    * [【${evi.adviceItem}】](#${evi.adviceItem})
    </#list>

</#if>
<!-- TOC -->



# 【规则名】
${ruleCode}
# 【规则说明】
<#list ruleDescs as field>
* ${field}
</#list>
# 【规则类型】
${ruleName}

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
<#list promptFields as field>
* ${field}
</#list>

-------------------
<#if specialDesc>
# 【循证依据】
<#list itemEvidenceInfos as evi>
### 【${evi.adviceItem}】
| 模块名称 | 指标名称 | 提示标题 | 提示内容 |
|------|------|------|------|
<#list evi.evidenceDetailInfos as detail>
| ${detail.moduleName}   | ${detail.indexName}   |  ${detail.tipTitle}   |  ${detail.tipContent}  |
</#list>
</#list>
</#if>