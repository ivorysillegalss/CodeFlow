package org.chenzc.codeflow.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.SysOptionsMapper;
import org.chenzc.codeflow.utils.ServerUtil;
import org.chenzc.codeflow.utils.TemplateUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CollectOptionsTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private SysOptionsMapper sysOptionsMapper;

    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {
        JudgeTask judgeTask = taskContext.getBusinessContextData();
        Submission submission = judgeTask.getSubmission();
        Problem problem = judgeTask.getProblem();
        String language = submission.getLanguage();

//        TODO 打缓存
        List<SysOptions> sysOptions = sysOptionsMapper.selectList(new QueryWrapper<SysOptions>()
                .eq("key", "languages"));
        if (CollUtil.isEmpty(sysOptions)) {
            log.error("cannnot find target languages config source");
        }
        String subConfigStr = CollUtil.getFirst(sysOptions).getValue();
        List<ProgrammingLanguage> subConfigs = JSON.parseArray(subConfigStr, ProgrammingLanguage.class);
        Optional<ProgrammingLanguage> subConfigOptional = subConfigs.stream()
                .filter(item -> language.equals(item.getName()))
                .findFirst();

        LanguageConfig languageConfig = null;
        if (subConfigOptional.isPresent()) {
            languageConfig = subConfigOptional.get().getConfig();
        }
        judgeTask.setLanguageConfig(languageConfig);

        if (CollUtil.isEmpty(sysOptions)) {
            log.error("cannnot find target languages config source");
//            TODO
        }

        Spj spj = null;
        if (StringUtils.isNotBlank(problem.getSpjCode())) {
            String spjLanguage = problem.getSpjLanguage();
            Optional<ProgrammingLanguage> spjConfigOptional = subConfigs.stream()
                    .filter(item -> Objects.nonNull(item.getSpj()) && item.getName().equals(spjLanguage))
                    .findFirst();
            if (spjConfigOptional.isPresent()) {
                spj = spjConfigOptional.get().getSpj();
            } else {
                log.error("cannnot find target languages config source");
//                TODO
            }
        }
        judgeTask.setSpj(spj);


        String templateString = problem.getTemplate();
        if (StringUtils.isNotBlank(templateString)) {
            Map<String, String> templateMap = JSON.parseObject(templateString, new TypeReference<>() {
            });
            String templateStr = templateMap.get(language);
            if (StringUtils.isBlank(templateStr)) {
//                TODO
            }
            Template template = TemplateUtil.parseProblemTemplate(templateStr);
            submission.setCode(TemplateUtil.generateCode(template, submission.getCode()));
        }
        judgeTask.setSubmission(submission);


    }
}
