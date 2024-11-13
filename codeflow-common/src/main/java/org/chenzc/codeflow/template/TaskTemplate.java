package org.chenzc.codeflow.template;


import lombok.AllArgsConstructor;
import lombok.Builder;
import org.chenzc.codeflow.executor.TaskNodeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author chenz
 * @date 2024/05/21
 * @Usage 工厂类方法 将不同的业务（节点）连起来形成链子
 */

@Builder
@AllArgsConstructor
//此处链子类没有使用泛型 对于同一条链子来说 他们的任务类型是相同的 所有不会产生类型错误
//而对于不同的链子 他们共享的是不同的TaskTemplate对象所以也不会产生错误
public class TaskTemplate {
    private List<TaskNodeModel> taskTemplate;

    public TaskTemplate() {
        taskTemplate = new ArrayList<>();
    }


    /**
     * @param taskNodeModel 新增节点
     *                      往元素中新增节点
     */
    public void put(TaskNodeModel taskNodeModel) {
        taskTemplate.add(taskNodeModel);
    }

    /**
     * @param taskNodeModels 新增节点
     *                       新增多个节点
     */
    public void putAll(Collection<TaskNodeModel> taskNodeModels) {
        taskTemplate.addAll(taskNodeModels);
    }

    public List<TaskNodeModel> get(){
        return taskTemplate;
    }
}
