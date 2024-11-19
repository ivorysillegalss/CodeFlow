package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Problem {
    private String _id;
    private String id;
    private String title;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String samples;

    private String testCaseId;

    //    TODO 同json字符串
    private String testCaseScore;
    private String hint;

    private String languages;

    //    此处的Template是各语言的模板 本质上是一个map
//    序列化后成为json键值对字符串
    private String template;

    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;

    private Integer timeLimit;
    private Integer memoryLimit;

    // Special judge-related fields
    private Boolean spj;
    private String spjLanguage;
    private String spjCode;
    private String spjVersion;
    private Boolean spjCompileOk;

    private String ruleType;
    private Boolean visible;
    private String difficulty;
    private String source;
    private long submissionNumber;
    private long acceptedNumber;

    private Integer createdById;
    private User createdBy;

    /**
     * 0 一般表示“Accepted”或“通过”，即解答正确。
     * -1 通常代表“Wrong Answer”或“答案错误”。
     * 负值如 -2 可能表示“Time Limit Exceeded”或其他错误状态（例如内存超限等）。
     * 正值如 4 或其他数字可能用于表示编译错误或特定类型的错误。
     *
     * @as HashMap<String, Integer>
     */
    private String statisticInfo;
    private int totalScore;

    private Boolean isPublic;

    private Integer contestId;
//    TODO 将Contest对象从 Problem中抽离出来
    private Contest contest;

    private String ioMode;
    private Boolean shareSubmission;
}
