package com.chenzc.codeflow.controller;

import com.chenzc.codeflow.domain.BasicResult;
import com.chenzc.codeflow.domain.Submission;
import com.chenzc.codeflow.service.JudgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class SubmissionController {
    @Resource
    private JudgeService judgeService;

    @PostMapping("/submission")
//    TODO 定义登录注解拦截器TBD
    public BasicResult applySubmission(@RequestBody Submission submission) {
        return judgeService.commitJudge(submission);
    }

    //    本质是一个回调接口 提供rpc judgeServer 之后返回的判题结果
    @GetMapping("/submission}")
    public BasicResult getSubmissionInfo(@RequestParam(value = "id",defaultValue = "") String id) {
        return judgeService.getSubmissionResult(id);
    }
}
