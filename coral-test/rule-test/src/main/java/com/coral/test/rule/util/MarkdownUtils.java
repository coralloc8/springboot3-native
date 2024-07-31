package com.coral.test.rule.util;


import com.coral.test.rule.config.RuleProperty;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Markdown 工具类
 *
 * @author huss
 * @date 2024/7/17 14:21
 * @packageName com.coral.test.rule.util
 * @className MarkdownUtils
 */
public class MarkdownUtils {
    public static final String TABLE_CSS_NAME = "rule";

    public static final String CSS_FILE_NAME = "rule_report_css.html";

    /**
     * markdown转html
     *
     * @param markdown    markdown文本
     * @param cssName     表格css样式名称
     * @param cssFilePath 表格css样式文件路径
     * @param cssFileName 表格css样式文件名称
     * @return
     */
    public static String markdownToHtml(String markdown, String cssName, String cssFilePath, String cssFileName) {
        String css = RuleParseHelper.getInstance().readFileContent(cssFilePath, cssFileName);

        MutableDataSet mutableDataSet = new MutableDataSet(PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL));
        // 表格样式
        mutableDataSet.set(TablesExtension.CLASS_NAME, cssName);

        Parser parser = Parser.builder(mutableDataSet).build();
        Document document = parser.parse(markdown);

        HtmlRenderer renderer = HtmlRenderer.builder(mutableDataSet).build();
        String body = renderer.render(document);

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(css)) {
            sb.append(css).append("\n\n");
        }
        sb.append(body);
        return sb.toString();
    }

    public static String markdownToHtml(String markdown) {
        String basePath = RuleProperty.REPORT_TEMPLATE_PATH;
        return markdownToHtml(markdown, TABLE_CSS_NAME, basePath, CSS_FILE_NAME);
    }

}
