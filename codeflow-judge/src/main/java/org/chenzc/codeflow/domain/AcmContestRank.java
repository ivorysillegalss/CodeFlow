package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Accessors(chain = true)
public class AcmContestRank extends ContestRank {
    private Integer acceptedNumber;
    private Float totalTime;
}
