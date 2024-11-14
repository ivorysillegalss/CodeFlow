package org.chenzc.codeflow.task;

import com.alibaba.fastjson.JSON;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.constant.CommonConstant;
import org.chenzc.codeflow.constant.RedisConstants;
import org.chenzc.codeflow.domain.User;
import org.chenzc.codeflow.domain.CommitTask;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextResponse;
import org.chenzc.codeflow.enums.RespEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 检查传入的值是否正确
 *
 * @author chenz
 * @date 2024/11/12
 */
@Slf4j
@Service
public class PreCheckTask implements TaskNodeModel<CommitTask> {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void execute(TaskContext<CommitTask> taskContext) {
        CommitTask commitTask = taskContext.getBusinessContextData();
        String code = commitTask.getCode();
        String language = commitTask.getLanguage();
        Integer problemId = commitTask.getProblemId();

        if (StringUtils.isNotBlank(code) || StringUtils.isNotBlank(language) || Objects.nonNull(problemId) || problemId.intValue() > 0) {
            setBadClientParameterResponse(taskContext);
            return;
        }

        if (StringUtils.isBlank(commitTask.getSessionId())) {
            setBadClientParameterResponse(taskContext);
            return;
        }

        String jsonUser = redisUtils.get(org.apache.commons.lang3.StringUtils.join(RedisConstants.USER_SESSION,
                CommonConstant.INFIX, commitTask.getSessionId()));
        if (StringUtils.isBlank(jsonUser)) {
            setBadClientParameterResponse(taskContext);
            return;
        }

        User user = JSON.parseObject(jsonUser, User.class);
        taskContext.getBusinessContextData().setUser(user);

        if (user.getIsDisabled()) {
            taskContext.setException(Boolean.TRUE)
                    .setResponse(TaskContextResponse.<CommitTask>builder()
                            .error("User has been banned")
                            .code(RespEnums.USER_BE_BANNED.getCode())
                            .build());
        }


        Integer contestId = commitTask.getContestId();
//        通过判断这里有无contestId 来判断是否比赛中的题目
//        1 非
        if (Objects.isNull(contestId)) {
            taskContext.getBusinessContextData().setIsContest(Boolean.FALSE);
        } else {
//            2 是
            taskContext.getBusinessContextData().setIsContest(Boolean.TRUE);
        }
    }


    public static void setBadClientParameterResponse(TaskContext<CommitTask> taskContext) {
        taskContext.setException(Boolean.TRUE)
                .setResponse(TaskContextResponse.<CommitTask>builder()
                        .code(RespEnums.CLIENT_BAD_VARIABLES.getCode())
                        .build());
    }
}
