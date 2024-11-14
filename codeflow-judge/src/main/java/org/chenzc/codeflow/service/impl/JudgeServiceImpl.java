package org.chenzc.codeflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.mapper.ProblemMapper;
import org.chenzc.codeflow.service.JudgeService;
import org.chenzc.codeflow.utils.ServerUtil;
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

    @Override
    public void send(CommitTask commitTask) {
        Problem problem = commitTask.getProblem();
        Submission submission = commitTask.getSubmission();
        String language = submission.getLanguage();
//        TODO 建表 在数据库中 存储语言相关配置并读取

        HashMap<String, String> spjConfig = new HashMap<>();
        if (Objects.nonNull(problem.getSpjCode())) {
            problem.getSpjLanguage();
//            TODO SPJ相关配置读取语言 此处遍历 反序化到spjConfig中
        }

        String template = problem.getTemplate();
//        TODO Template判断处理 构建判题模板 并将模板内容融合到code中
        submission.setCode(template);
        JudgeData judgeData = assembleJudgeData(submission, problem, spjConfig, language);

        JudgeServer judgeServer = serverUtil.getJudgeServer();
        if (Objects.nonNull(judgeServer)){
            serverUtil.afterJudgeServerGet(judgeServer);
        }
//        TODO 将任务打入缓冲队列等待


    }

    public static JudgeData assembleJudgeData(Submission submission, Problem problem, HashMap<String, String> spjConfig, String language) {
        return JudgeData.builder().languageConfig(language)
                .src(submission.getCode())
                .maxMemory(JudgeConstant.DEFAULT_MAX_MEMORY_PRE * problem.getMemoryLimit())
                .maxCpuTime(problem.getTimeLimit())
                .testCaseId(problem.getTestCaseId())
                .output(Boolean.FALSE)
                .spjVersion(problem.getSpjVersion())
                .spjConfig(spjConfig.get("config"))
                .spjCompileConfig(spjConfig.get("compile"))
                .spjSrc(problem.getSpjCode())
                .ioMode(problem.getIoMode()).build();
    }
}

