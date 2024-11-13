package org.chenzc.codeflow.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.chenzc.codeflow.domain.User;

@Builder
@Getter
@Setter
public class CommitTask extends TaskContextData {
    /**
     * 指提交的代码
     */
    private String code;
    private Integer contestId;
    private String language;
    private Integer problemId;

    private Boolean isContest;
    private String password;

    private String sessionId;
    private User user;
    private Boolean isHide;

    private String submissionId;
}
