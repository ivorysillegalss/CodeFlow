package com.chenzc.codeflow.service.impl;

import com.chenzc.codeflow.domain.BasicResult;
import com.chenzc.codeflow.domain.Submission;
import com.chenzc.codeflow.service.JudgeService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class JudgeServiceImpl implements JudgeService {


    @Override
    public BasicResult commitJudge(Submission submission) {
        if (Objects.isNull(submission.getContestId())) {

        }
    }

    //    TODO 鉴权 自定义注解
    public String checkContestPermission(Submission submission) {

    }

    @Override
    public BasicResult getSubmissionResult(String id) {
        return null;
    }
}
