package com.chenzc.codeflow.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.Submission;

import jakarta.annotation.Resource;
import org.chenzc.codeflow.service.JudgeService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/refactor/api/submission")
public class SubmissionController {
    @Resource
    private JudgeService judgeService;

    @PostMapping
//    TODO 定义登录注解拦截器TBD
    public BasicResult applySubmission(@RequestBody Submission submission, HttpServletRequest request) {
        return judgeService.commitJudge(submission, request);
    }

    //    本质是一个回调接口 提供rpc judgeServer 之后返回的判题结果
    @GetMapping
    public BasicResult getSubmissionInfo(@RequestParam(value = "id", defaultValue = "") String id) {
        return judgeService.getSubmissionResult(id);
    }
}
