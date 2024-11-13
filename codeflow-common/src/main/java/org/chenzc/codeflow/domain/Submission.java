package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
public class Submission {
    private String code;
    private Integer contestId;
    private String language;
    private Integer problemId;
    private String username;

    private String submissionId;

    private String ip;
}
