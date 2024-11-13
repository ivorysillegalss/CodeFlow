package org.chenzc.codeflow.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.entity.CommitTask;
import org.chenzc.codeflow.entity.Contest;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.entity.TaskContextResponse;
import org.chenzc.codeflow.enums.ContestRuleType;
import org.chenzc.codeflow.enums.ContestStatus;
import org.chenzc.codeflow.enums.ContestType;
import org.chenzc.codeflow.enums.RespEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.ContestMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 如果是比赛的话 这里赋予比赛的相关信息 不是比赛就开摆
 *
 * @author chenz
 * @date 2024/11/12
 */
@Slf4j
@Service
public class CheckContestTask implements TaskNodeModel<CommitTask> {

    @Resource
    private ContestMapper contestMapper;

    @Override
    public void execute(TaskContext<CommitTask> taskContext) {
        Boolean isContest = taskContext.getBusinessContextData().getIsContest();
        if (!isContest) {
            taskContext.setException(Boolean.TRUE)
                    .setResponse(TaskContextResponse.<CommitTask>builder().
                            code(RespEnums.CLIENT_BAD_PARAMETERS.getCode())
                            .build());
            return;
        }
        Integer contestId = taskContext.getBusinessContextData().getContestId();
        QueryWrapper<Contest> qw = new QueryWrapper<Contest>();
        qw.eq("contest_id", contestId).eq("visible", true);
        List<Contest> contests = contestMapper.selectList(qw);
        if (CollUtil.isEmpty(contests)) {
            taskContext.setException(Boolean.TRUE)
                    .setResponse(TaskContextResponse.<CommitTask>builder().
                            code(RespEnums.CLIENT_BAD_PARAMETERS.getCode())
                            .error("Contest " + contestId + " doesn't exist")
                            .build());
            return;
        }
//            TODO 补充拦截器 拦截未登录请求

        Contest contest = CollUtil.getFirst(contests);
//            如果是密码保护的比赛 验证密码
        if (contest.getContestType().equals(ContestType.PASSWORD_PROTECTED_CONTEST.getMessage())) {
            if (contest.getPassword().equals(taskContext.getBusinessContextData().getPassword())) {
                taskContext.setException(Boolean.TRUE).setResponse(TaskContextResponse.
                        <CommitTask>builder()
                        .error("Wrong password or password expired")
                        .code(RespEnums.PASSWORD_CHECK_ERROR.getCode())
                        .build());
            }
        }

        setContestStatus(contest);

//            如果比赛还没有开始 就想要答题
        if (contest.getStatus().equals(ContestStatus.CONTEST_NOT_START.getCode())) {
            taskContext.setException(Boolean.TRUE).setResponse(TaskContextResponse.
                    <CommitTask>builder()
                    .error("Contest has not started yet.")
                    .code(RespEnums.CONTEST_NOT_START.getCode())
                    .build());
        }

        taskContext.getBusinessContextData()
                .setIsHide(problemDetailsPermission(contest,
                        taskContext.getBusinessContextData().getUser().getId()));

    }

    public static void setContestStatus(Contest contest) {
        if (contest.getStartTime().isAfter(LocalDateTime.now()))
            contest.setStatus(ContestStatus.CONTEST_NOT_START.getCode());
        else if (contest.getEndTime().isBefore(LocalDateTime.now())) {
            contest.setStatus(ContestStatus.CONTEST_ENDED.getCode());
        } else contest.setStatus(ContestStatus.CONTEST_UNDERWAY.getCode());
    }

    public static Boolean problemDetailsPermission(Contest contest, Integer creatorId) {
        return contest.getRuleType().equals(ContestRuleType.ACM.getCode())
                || contest.getStatus().equals(ContestStatus.CONTEST_ENDED.getCode())
                || contest.getCreatedById().equals(creatorId)
                || contest.getRealTimeRank();
    }
}
