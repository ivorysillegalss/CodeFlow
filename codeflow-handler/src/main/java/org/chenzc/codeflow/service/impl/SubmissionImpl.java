package org.chenzc.codeflow.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.Submission;
import org.chenzc.codeflow.domain.CommitTask;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextData;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.service.SubmissionService;
import org.chenzc.codeflow.template.TaskController;
import org.chenzc.codeflow.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SubmissionImpl implements SubmissionService {

    @Autowired
    @Qualifier("CommitTaskController")
    private TaskController taskController;

    @Override
    public BasicResult commitJudge(Submission submission, HttpServletRequest request) {
        CommitTask commitTask = CommitTask.builder()
                .problemId(submission.getProblemId())
                .contestId(submission.getContestId())
                .language(submission.getLanguage())
                .code(submission.getCode())
                .ip(ServletUtils.getClientIpAddress(request))
//                                                     TODO   .contestPassword()
                .build();

        TaskContext<TaskContextData> commitContext = TaskContext.builder()
                .businessCode(BusinessEnums.COMMIT.getCode())
                .businessType(BusinessEnums.COMMIT.getMessage())
                .businessContextData(commitTask)
                .build();

        TaskContext<TaskContextData> taskContext = taskController.executeChain(commitContext);

        return BasicResult.builder().error(taskContext.getResponse().getError())
                .data(taskContext.getResponse().getData()).build();
    }

//        TODO 鉴权 自定义注解
//    public String checkContestPermission(Submission submission) {
//
//    }

    @Override
    public BasicResult getSubmissionResult(String id) {
        return null;
    }
}
