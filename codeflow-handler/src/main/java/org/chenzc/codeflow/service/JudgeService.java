package org.chenzc.codeflow.service;


import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.Submission;
import jakarta.servlet.http.HttpServletRequest;

public interface JudgeService {
    BasicResult commitJudge(Submission submission, HttpServletRequest request);

    BasicResult getSubmissionResult(String id);
}