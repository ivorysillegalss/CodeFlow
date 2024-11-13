package org.chenzc.codeflow.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.chenzc.codeflow.domain.Submission;
import org.chenzc.codeflow.domain.User;
import org.chenzc.codeflow.entity.CommitTask;
import org.chenzc.codeflow.entity.Problem;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextResponse;
import org.chenzc.codeflow.enums.RespEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.ProblemMapper;
import org.chenzc.codeflow.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;

@Service
@Slf4j
public class CheckProblemTask implements TaskNodeModel<CommitTask> {

    @Resource
    private SubmissionMapper submissionMapper;

    @Resource
    private ProblemMapper problemMapper;

    @Override
    public void execute(TaskContext<CommitTask> taskContext) {
        CommitTask commitTask = taskContext.getBusinessContextData();
        QueryWrapper<Problem> qw = new QueryWrapper<>();
        qw.eq("problem_id", commitTask.getProblemId())
//                TODO
                .eq("contest_id", commitTask.getContestId())
                .eq("visible", Boolean.TRUE);
        List<Problem> problems = problemMapper.selectList(qw);
        if (CollUtil.isEmpty(problems)) {
            setException(taskContext, "Problem not exist");
            return;
        }
        Problem problem = CollUtil.getFirst(problems);

        List<String> languages = JSON.parseArray(problem.getLanguages(), String.class);
        boolean isLanguageExist = languages
                .contains(commitTask.getLanguage());

        if (!isLanguageExist) {
            setException(taskContext, StringUtils.join(commitTask.getLanguage(), "  is not allowed in the problem"));
        }

        Submission submission = Submission.builder()
                .submissionId(IdUtil.simpleUUID())
                .language(commitTask.getLanguage())
                .code(commitTask.getCode())
                .problemId(commitTask.getProblemId())
                .contestId(commitTask.getContestId())
                .username(commitTask.getUser().getUsername())
//                TODO 获取ip？
//                .ip()
                .build();
        submissionMapper.insert(submission);

//        TODO 此处线程池send任务


        TaskContextResponse<CommitTask> successResp = TaskContextResponse
                .<CommitTask>builder().code(RespEnums.SUCCESS.getCode()).build();
        if (commitTask.getIsHide()) {
            taskContext.setResponse(successResp);
        } else {
            successResp.setData(CommitTask.builder().submissionId(submission.getSubmissionId()).build());
            taskContext.setResponse(successResp);
        }
    }

    private static void setException(TaskContext<CommitTask> taskContext, String error) {
        taskContext.setException(Boolean.TRUE)
                .setResponse(TaskContextResponse
                        .<CommitTask>builder()
                        .error(error)
                        .code(RespEnums.CLIENT_BAD_PARAMETERS.getCode())
                        .build());
    }
}
