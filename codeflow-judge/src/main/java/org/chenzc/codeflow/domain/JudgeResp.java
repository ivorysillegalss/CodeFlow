package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
public class JudgeResp {
    private Object error;
    private List<JudgeRespData> data;
}
