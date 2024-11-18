package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserProfile {
    private Integer id;

    /**
     * JSON字符串
     */
    private String acmProblemsStatus;
    private String avatar;
    private String  blog;
    private String mood;
    private Integer acceptedNumber;
    private Integer submissionNumber;
    private String github;
    private String school;
    private String major;
    private String userId;
    private Integer totalScore;
    private String oiProblemsStatus;
    private String realName;
    private String language;
}
