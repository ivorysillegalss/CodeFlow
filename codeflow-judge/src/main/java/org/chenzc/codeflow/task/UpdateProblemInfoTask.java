package org.chenzc.codeflow.task;

import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.domain.JudgeTask;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateProblemInfoTask implements TaskNodeModel<JudgeTask> {
    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {

    }
}
