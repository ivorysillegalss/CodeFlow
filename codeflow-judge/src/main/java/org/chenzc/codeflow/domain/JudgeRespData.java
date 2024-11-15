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
    private Integer memory;
    private Integer result;
    private Integer score;
}
