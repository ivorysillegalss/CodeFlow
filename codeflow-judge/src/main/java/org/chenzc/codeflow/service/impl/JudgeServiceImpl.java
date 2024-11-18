package org.chenzc.codeflow.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextData;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.service.JudgeService;
import org.chenzc.codeflow.template.TaskController;
import org.chenzc.codeflow.utils.ServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

//    TODO 再分割一层责任链

    @Resource
    private ServerUtil serverUtil;

    @Autowired
    @Qualifier(" JudgeProblemController")
    private TaskController taskController;

    @Override
    public void send(CommitTask commitTask) {
        JudgeTask judgeTask = JudgeTask.builder()
                .isContest(commitTask.getIsContest())
                .submission(commitTask.getSubmission())
                .problem(commitTask.getProblem())
                .language(commitTask.getLanguage())
                .user(commitTask.getUser()).build();

        TaskContext<TaskContextData> judgeContext = TaskContext.builder()
                .businessCode(BusinessEnums.JUDGE.getCode())
                .businessType(BusinessEnums.JUDGE.getMessage())
                .businessContextData(judgeTask)
                .build();

        TaskContext<TaskContextData> taskContext = taskController.executeChain(judgeContext);

//     TODO 返回做处理

//        return BasicResult.builder().error(taskContext.getResponse().getError())
//                .data(taskContext.getResponse().getData()).build();
//
    }

}

