package org.chenzc.codeflow.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ContestSubmissionInfo {
    private Boolean isAc;
    private Float acTime;
    private Boolean isFirstAc;
    private Integer errorNumber;
}
