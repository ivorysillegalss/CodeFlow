package org.chenzc.codeflow.config;

import jakarta.annotation.Resource;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.task.*;
import org.chenzc.codeflow.template.TaskController;
import org.chenzc.codeflow.template.TaskTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServerChainConfig {

    @Resource
    private CollectOptionsTask collectOptionsTask;

    @Resource
    private AssembleDataTask assembleDataTask;

    @Resource
    private ExecuteJudgeTask executeJudgeTask;

    @Resource
    private ParseRespTask parseRespTask;

    @Resource
    private UpdateContestProblemInfoTask updateContestProblemInfoTask;

    @Resource
    private UpdateNormalProblemInfoTask updateNormalProblemInfoTask;

    @Bean("judgeTemplate")
    public TaskTemplate judgeTemplate() {
        return TaskTemplate.builder()
                .taskTemplate(Arrays.asList(collectOptionsTask, assembleDataTask, executeJudgeTask
                        , parseRespTask, updateContestProblemInfoTask, updateNormalProblemInfoTask))
                .build();
    }

    @Bean("JudgeProblemController")
    public TaskController taskController() {
        TaskController taskController = TaskController.builder().build();
        Map<String, TaskTemplate> taskTemplates = new HashMap<>();
//        由于设计原因 不同的链子（不同的TaskTemplate中） 所对应的上下文数据类型不相同
        taskTemplates.put(BusinessEnums.COMMIT.getCode(), judgeTemplate());
        taskController.setTaskTemplates(taskTemplates);
        return taskController;
    }
}
