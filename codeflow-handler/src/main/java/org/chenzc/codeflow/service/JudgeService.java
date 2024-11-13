package org.chenzc.codeflow.service;


import jakarta.servlet.http.HttpServletRequest;
import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.Submission;

public interface JudgeService {
    BasicResult commitJudge(Submission submission, HttpServletRequest request);

    BasicResult getSubmissionResult(String id);
}