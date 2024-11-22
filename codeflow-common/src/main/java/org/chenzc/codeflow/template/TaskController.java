package org.chenzc.codeflow.template;

import lombok.Builder;
import lombok.Setter;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextData;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author chenz
 * @date 2024/05/21
 * 责任链中真正执行任务的类
 * 按理来说一个成功执行任务的责任链流程是一个bean
 * 但是可以在实例化bean的时候 结合@Qualfier注解 精确指定实例化的是哪个bean
 */

@Builder
@Setter
@Component
public class TaskController {
    private Map<String, TaskTemplate> taskTemplates;


    /**
     *
     * @param taskContext 表示任务执行的上下文
     * @return {@link TaskContext }
     */
    public TaskContext<TaskContextData> executeChain(TaskContext<TaskContextData> taskContext) {
        TaskTemplate taskTemplate = taskTemplates.get(taskContext.getBusinessCode());
        List<TaskNodeModel> taskList = taskTemplate.get();
        for (TaskNodeModel task : taskList) {
            task.execute(taskContext);
            if (taskContext.getException()) {
                return taskContext;
            }
        }
        return taskContext;
    }
}
