package org.chenzc.codeflow.config;

import jakarta.annotation.Resource;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.task.CheckContestTask;
import org.chenzc.codeflow.task.CheckProblemTask;
import org.chenzc.codeflow.task.PreCheckTask;
import org.chenzc.codeflow.task.SendJudgeTask;
import org.chenzc.codeflow.template.TaskController;
import org.chenzc.codeflow.template.TaskTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenz
 * @date 2024/05/21
 * 责任链配置类 接入层所有的业务配置
 * 维护多个类
 */
@Configuration
public class ServiceChainConfig {

    //    下方四个是具体的接入层业务
    @Resource
    private PreCheckTask preCheckTask;
    @Resource
    private CheckContestTask checkContestTask;
    @Resource
    private CheckProblemTask checkProblemTask;
    @Resource
    private SendJudgeTask sendJudgeTask;

    @Bean("commitTemplate")
    public TaskTemplate commitTemplate() {
        return TaskTemplate.builder()
                .taskTemplate(Arrays.asList(preCheckTask, checkContestTask, checkProblemTask, sendJudgeTask))
                .build();
    }

    @Bean("CommitTaskController")
    public TaskController taskController() {
        TaskController taskController = TaskController.builder().build();
        Map<String, TaskTemplate> taskTemplates = new HashMap<>();
//        由于设计原因 不同的链子（不同的TaskTemplate中） 所对应的上下文数据类型不相同
        taskTemplates.put(BusinessEnums.COMMIT.getCode(), commitTemplate());
        taskController.setTaskTemplates(taskTemplates);
        return taskController;
    }
}
