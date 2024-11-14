package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.chenzc.codeflow.entity.TaskContextData;

@Builder
@Getter
@Setter
public class JudgeTask extends TaskContextData {
    private Submission submission;
    private Problem problem;
    private String language;
    private Spj spj;
    private LanguageConfig languageConfig;
}
