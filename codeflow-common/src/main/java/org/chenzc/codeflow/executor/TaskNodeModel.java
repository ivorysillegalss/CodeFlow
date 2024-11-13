package org.chenzc.codeflow.executor;

import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextData;

/**
 * @author chenz
 * @date 2024/05/21
 * 任何责任链中的节点都需要实现该接口
 */
public interface TaskNodeModel<T extends TaskContextData> {
    void execute(TaskContext<T> taskContext);

}