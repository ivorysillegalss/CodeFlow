package org.chenzc.codeflow.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ContestSubmissionInfo {
    private Boolean isAc;
    private Float acTime;
    private Boolean isFirstAc;
    private Integer errorNumber;
}
