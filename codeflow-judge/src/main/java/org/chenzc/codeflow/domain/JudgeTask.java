package org.chenzc.codeflow.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.chenzc.codeflow.entity.TaskContextData;

@Builder
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class JudgeTask extends TaskContextData {
    private Boolean isContest;
    private Submission submission;
    private Problem problem;
    private String language;
    private Spj spj;
    private LanguageConfig languageConfig;
    private JudgeServer judgeServer;
    private JudgeData judgeData;
    private User user;
    private JudgeResp judgeResp;
    private JudgeStaticInfo judgeStaticInfo;
    private ContestRank contestRank;
}
