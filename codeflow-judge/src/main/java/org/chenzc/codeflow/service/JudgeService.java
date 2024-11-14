package org.chenzc.codeflow.service;

import org.chenzc.codeflow.domain.CommitTask;
import org.chenzc.codeflow.domain.Submission;

public interface JudgeService {
    void send(CommitTask commitTask);
}
