package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AcmContestRank extends ContestRank {
    private Integer acceptedNumber;
    private Integer totalTime;
}
