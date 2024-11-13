package org.chenzc.codeflow.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chenz
 * @date 2024/05/21
 * 表示责任链的状态 (上下文)
 * （节点运行到每一个任务时的状态）
 */
@Builder
@Data
@Accessors(chain = true)
public class TaskContext<T extends TaskContextData> {
    /**
     * 表示当前所执行的业务类型
     */
    private String businessType;

    /**
     * 表示当前所执行的业务的号码
     */
    private String businessCode;

    /**
     * 表示对应的上下文数据
     */
    private T businessContextData;

    /**
     * 表示当前业务执行有无出错
     */
    private Boolean Exception;

    private TaskContextResponse<T> response;
}
