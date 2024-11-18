package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JudgeRespData {
    private String testCase;
    private Integer cpuTime;
    private Integer realTime;
    private Integer memory;
    private Integer signal;
    private Integer exitCode;
    private String error;
    private Integer result;
    private Integer score;
}
