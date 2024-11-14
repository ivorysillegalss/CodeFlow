package org.chenzc.codeflow.task;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.JudgeResEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.SubmissionMapper;
import org.chenzc.codeflow.utils.ServerUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
@Slf4j
public class AssembleDataTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private ServerUtil serverUtil;

    @Resource
    private SubmissionMapper submissionMapper;

    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {

        JudgeTask judgeTask = taskContext.getBusinessContextData();
        Submission submission = judgeTask.getSubmission();
        Problem problem = judgeTask.getProblem();
        Spj spj = judgeTask.getSpj();

        JudgeData judgeData = assembleJudgeData(submission, problem, spj);

        JudgeServer judgeServer = serverUtil.getJudgeServer();
        if (Objects.isNull(judgeServer)) {
//        TODO 将任务打入Redis缓冲队列等待
        }
        serverUtil.afterJudgeServerGet(judgeServer);

        submissionMapper.updateById(submission.setResult(Integer.parseInt(JudgeResEnums.JUDGING.getCode())));

        taskContext.setBusinessContextData(judgeTask.setJudgeData(judgeData)
                .setJudgeServer(judgeServer));
    }

    public static JudgeData assembleJudgeData(Submission submission, Problem problem, Spj spj) {
        return JudgeData.builder().languageConfig(submission.getLanguage())
                .src(submission.getCode())
                .maxMemory(JudgeConstant.DEFAULT_MAX_MEMORY_PRE * problem.getMemoryLimit())
                .maxCpuTime(problem.getTimeLimit())
                .testCaseId(problem.getTestCaseId())
                .output(Boolean.FALSE)
                .spjVersion(problem.getSpjVersion())
                .spjConfig(spj.getConfig())
                .spjCompileConfig(spj.getCompile())
                .spjSrc(problem.getSpjCode())
                .ioMode(problem.getIoMode()).build();
    }
}
