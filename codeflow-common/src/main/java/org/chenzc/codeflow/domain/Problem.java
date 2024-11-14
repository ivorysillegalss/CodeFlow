package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Problem {
    private String id;
    private String title;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String samples;

    private String testCaseId;
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

    private String statisticInfo;
    private int totalScore;

    private Boolean isPublic;

    private Integer contestId;
    private Contest contest;

    private String ioMode;
    private Boolean shareSubmission;
}
