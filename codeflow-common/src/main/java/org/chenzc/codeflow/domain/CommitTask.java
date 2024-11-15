package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.chenzc.codeflow.domain.Submission;
import org.chenzc.codeflow.domain.User;
import org.chenzc.codeflow.entity.TaskContextData;

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
    private String contestPassword;

    private String sessionId;
    private User user;
    private Boolean isHide;

    private String submissionId;
    private String ip;

    private Problem problem;
    private Contest contest;
    private Submission submission;
}
