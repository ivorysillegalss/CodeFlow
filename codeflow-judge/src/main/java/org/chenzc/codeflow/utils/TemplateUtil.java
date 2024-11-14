package org.chenzc.codeflow.utils;

import org.chenzc.codeflow.domain.Template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateUtil {

    // 方法来解析模板字符串
    public static Template parseProblemTemplate(String templateStr) {
        // 正则表达式来匹配各个部分
        String prependPattern = "//PREPEND BEGIN\\n([\\s\\S]+?)//PREPEND END";
        String templatePattern = "//TEMPLATE BEGIN\\n([\\s\\S]+?)//TEMPLATE END";
        String appendPattern = "//APPEND BEGIN\\n([\\s\\S]+?)//APPEND END";

        // 提取内容
        String prepend = extractContent(templateStr, prependPattern);
        String template = extractContent(templateStr, templatePattern);
        String append = extractContent(templateStr, appendPattern);

        // 返回封装好的Template对象
        return new Template(
                prepend.isEmpty() ? "" : prepend,
                template.isEmpty() ? "" : template,
                append.isEmpty() ? "" : append
        );
    }

    // 辅助方法用于根据正则表达式提取内容
    private static String extractContent(String str, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(str);
        return matcher.find() ? matcher.group(1) : "";
    }

    public static String generateCode(Template template, String submissionCode) {
        return String.format("%s\n%s\n%s", template.getPrepend(), submissionCode, template.getAppend());
    }
}
