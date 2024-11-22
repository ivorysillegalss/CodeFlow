package org.chenzc.codeflow.task;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.config.ThreadPoolConfig;
import org.chenzc.codeflow.domain.Submission;
import org.chenzc.codeflow.domain.CommitTask;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextResponse;
import org.chenzc.codeflow.enums.RespEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.service.JudgeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class SendJudgeTask implements TaskNodeModel<CommitTask> {


    @Resource
    private JudgeService judgeService;

    @Override
    public void execute(TaskContext<CommitTask> taskContext) {
        ExecutorService judgeExecutor = ThreadPoolConfig.getExecutor();

        CommitTask commitTask = taskContext.getBusinessContextData();
        Submission submission = commitTask.getSubmission();

        judgeExecutor.execute(() -> {
            judgeService.send(commitTask);
        });


        TaskContextResponse<CommitTask> successResp = TaskContextResponse
                .<CommitTask>builder().code(RespEnums.SUCCESS.getCode()).build();
        if (commitTask.getIsHide()) {
            taskContext.setResponse(successResp);
        } else {
            successResp.setData(CommitTask.builder().submissionId(submission.getSubmissionId()).build());
            taskContext.setResponse(successResp);
        }
    }
}
