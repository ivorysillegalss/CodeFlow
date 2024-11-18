package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JudgeTestCase {
    private Integer score;
    private String inputName;
    private String outputName;
}
