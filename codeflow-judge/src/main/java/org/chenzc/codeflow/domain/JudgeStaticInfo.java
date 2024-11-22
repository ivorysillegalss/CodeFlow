package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder

public class JudgeStaticInfo {
    private Integer timeCost;
    private Integer memoryCost;
    private List<JudgeRespData> errInfo;
    private Integer score;
}
