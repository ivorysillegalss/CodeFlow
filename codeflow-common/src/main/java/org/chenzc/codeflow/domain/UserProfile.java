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
     * {"contest_problems": {"373": {"_id": "1", "status": 1}, "379": {"_id": "4", "status": 0}, "389": {"_id": "9", "status": 0}}}
     */
    private String acmProblemsStatus;
    private String avatar;
    private String blog;
    private String mood;
    private Integer acceptedNumber;
    private Integer submissionNumber;
    private String github;
    private String school;
    private String major;
    private String userId;
    private Integer totalScore;
    /**
     * JSON字符串
     */
    private String oiProblemsStatus;
    private String realName;
    private String language;
}
