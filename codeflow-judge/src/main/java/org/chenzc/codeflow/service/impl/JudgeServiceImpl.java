package org.chenzc.codeflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextData;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.mapper.ProblemMapper;
import org.chenzc.codeflow.service.JudgeService;
import org.chenzc.codeflow.template.TaskController;
import org.chenzc.codeflow.utils.ServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
                .submission(commitTask.getSubmission())
                .problem(commitTask.getProblem())
                .language(commitTask.getLanguage()).build();

        TaskContext<TaskContextData> commitContext = TaskContext.builder()
                .businessCode(BusinessEnums.JUDGE.getCode())
                .businessType(BusinessEnums.JUDGE.getMessage())
                .businessContextData(judgeTask)
                .build();

        TaskContext<TaskContextData> taskContext = taskController.executeChain(commitContext);

//     TODO 返回做处理

//        return BasicResult.builder().error(taskContext.getResponse().getError())
//                .data(taskContext.getResponse().getData()).build();
//
    }

}

