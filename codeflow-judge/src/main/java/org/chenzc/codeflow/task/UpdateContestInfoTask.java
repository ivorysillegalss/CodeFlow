package org.chenzc.codeflow.task;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzc.codeflow.enums.JudgeStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.ContestRuleType;
import org.chenzc.codeflow.enums.ProblemRuleType;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.UserMapper;
import org.chenzc.codeflow.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UpdateContestInfoTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private UserProfileMapper userProfileMapper;

    @Transactional
    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {
        JudgeTask judgeTask = taskContext.getBusinessContextData();
        Boolean isContest = judgeTask.getIsContest();
        if (!isContest) {
            return;
        } else {
//            TODO 这里原项目有鉴权 判断比赛状态 此处忽略
            updateContestInfo(judgeTask);
        }
    }

    @Transactional()
    public void updateContestInfo(JudgeTask judgeTask) {
        List<UserProfile> users = userProfileMapper.selectList(new QueryWrapper<UserProfile>()
                .eq("id", judgeTask.getUser().getId()));
        if (CollUtil.isEmpty(users)) {
            log.error("cannot find target user with id: {}", judgeTask.getUser().getId());
            return;
        }
        String problemId = judgeTask.getProblem().getId();
        Submission submission = judgeTask.getSubmission();
        UserProfile userProfile = CollUtil.getFirst(users);
        String problemsStatusStr = userProfile.getAcmProblemsStatus();

//        判断是否ACM模式题目 做不同处理
        String ruleType = judgeTask.getProblem().getRuleType();
        if (ruleType.equals(ContestRuleType.ACM.getCode())) {
            AcmProblemStatus acmProblemStatus = JSON.parseObject(problemsStatusStr, AcmProblemStatus.class);
            HashMap<String, ProblemStatus> contestProblems = acmProblemStatus.getContestProblems();

            ProblemStatus problemStatus = contestProblems.get(problemId);
//            如果之前没有提交记录
            if (!contestProblems.containsKey(problemId)) {
//             TODO 题目 _id属性 相关问题
                contestProblems.put(problemId,
                        ProblemStatus.builder()
                                .status((submission.getResult()))
                                ._id(judgeTask.getProblem().get_id())
                                .build());

////                更新accept题目数
//                if (submission.getResult().toString().equals(JudgeStatus.ACCEPTED.getCode())){
//                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
//                }

//                如果之前有提交过 但是没有accept
            } else if (!problemStatus.getStatus().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                problemStatus.setStatus(submission.getResult());
                contestProblems.put(problemId, problemStatus);
            } else {
//                已经AC 过的话 不做刷新
                return;
            }
            String jsonContestProblemsStatus = JSON.toJSONString(contestProblems);
            userProfile.setAcmProblemsStatus(jsonContestProblemsStatus);
            userProfileMapper.updateById(userProfile);

        } else if (ruleType.equals(ContestRuleType.OI.getCode())){

        }

    }


}
