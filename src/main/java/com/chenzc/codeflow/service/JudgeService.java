package com.chenzc.codeflow.service;


import com.chenzc.codeflow.domain.BasicResult;
import com.chenzc.codeflow.domain.Submission;

public interface JudgeService {
    BasicResult commitJudge(Submission submission);

    BasicResult getSubmissionResult(String id);
}