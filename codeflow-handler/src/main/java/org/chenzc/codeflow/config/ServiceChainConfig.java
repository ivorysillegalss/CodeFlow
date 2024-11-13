package org.chenzc.codeflow.config;

import jakarta.annotation.Resource;
import org.chenzc.codeflow.enums.BusinessEnums;
import org.chenzc.codeflow.task.PreCheckTask;
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
    private AssembleInfoTask assembleInfoTask;
    @Resource
    private AfterCheckTask afterCheckTask;
    @Resource
    private SendMqTask sendMqTask;

    @Bean("sendTemplate")
    public TaskTemplate sendTemplate() {
        return TaskTemplate.builder()
                .taskTemplate(Arrays.asList(preCheckTask, assembleInfoTask, afterCheckTask, sendMqTask))
                .build();
    }

    @Bean("ServiceTaskController")
    public TaskController taskController() {
        TaskController taskController = TaskController.builder().build();
        Map<String, TaskTemplate> taskTemplates = new HashMap<>();
//        由于设计原因 不同的链子（不同的TaskTemplate中） 所对应的上下文数据类型不相同
        taskTemplates.put(BusinessEnums.COMMIT.getCode(), sendTemplate());
        taskController.setTaskTemplates(taskTemplates);
        return taskController;
    }
}
