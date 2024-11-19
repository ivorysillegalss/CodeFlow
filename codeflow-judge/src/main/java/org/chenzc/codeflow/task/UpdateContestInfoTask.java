package org.chenzc.codeflow.task;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenzc.codeflow.enums.JudgeStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.chenzc.codeflow.constant.CommonConstant;
import org.chenzc.codeflow.constant.ContestConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.ContestRuleType;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.AcmContestRankMapper;
import org.chenzc.codeflow.mapper.OiContestRankMapper;
import org.chenzc.codeflow.mapper.ProblemMapper;
import org.chenzc.codeflow.mapper.UserProfileMapper;
import org.chenzc.codeflow.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UpdateContestInfoTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private UserProfileMapper userProfileMapper;

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private AcmContestRankMapper acmContestRankMapper;

    @Resource
    private OiContestRankMapper oiContestRankMapper;

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
        updateContestProblemStatus(judgeTask);
        updateContestRank(judgeTask);
    }

    public void updateContestProblemStatus(JudgeTask judgeTask) {
        List<UserProfile> users = userProfileMapper.selectList(new QueryWrapper<UserProfile>()
                .eq("id", judgeTask.getUser().getId()));
        if (CollUtil.isEmpty(users)) {
            log.error("cannot find target user with id: {}", judgeTask.getUser().getId());
            return;
        }
        Problem problem = judgeTask.getProblem();
        String problemId = problem.getId();
        Submission submission = judgeTask.getSubmission();
        Integer submissionResult = submission.getResult();
        UserProfile userProfile = CollUtil.getFirst(users);
        String _id = problem.get_id();


//        判断是否ACM模式题目 做不同处理
        String ruleType = problem.getRuleType();
        if (ruleType.equals(ContestRuleType.ACM.getCode())) {
            String problemsStatusStr = userProfile.getAcmProblemsStatus();
            AcmProblemStatus acmProblemStatus = JSON.parseObject(problemsStatusStr, AcmProblemStatus.class);
            HashMap<String, ProblemStatus> contestProblems = acmProblemStatus.getContestProblems();

            ProblemStatus problemStatus = contestProblems.get(problemId);
//            如果之前没有提交记录
            if (!contestProblems.containsKey(problemId)) {
//             TODO 题目 _id属性 相关问题
                contestProblems.put(problemId,
                        ProblemStatus.builder()
                                .status(submissionResult)
                                ._id(_id)
                                .build());

////                更新accept题目数
//                if (submission.getResult().toString().equals(JudgeStatus.ACCEPTED.getCode())){
//                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
//                }

//                如果之前有提交过 但是没有accept
            } else if (!problemStatus.getStatus().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                problemStatus.setStatus(submissionResult);
                contestProblems.put(problemId, problemStatus);
            } else {
//                已经AC 过的话 不做刷新
                return;
            }
            String jsonContestProblemsStatus = JSON.toJSONString(contestProblems);
            userProfile.setAcmProblemsStatus(jsonContestProblemsStatus);

        } else if (ruleType.equals(ContestRuleType.OI.getCode())) {
//            OI模式下 渲染每一个用户的得分 此处先判断是否答过
//            得出此次做题的得分 并确认是否覆盖
            String oiProblemStatusStr = userProfile.getOiProblemsStatus();
            OiProblemStatus oiProblemStatus = JSON.parseObject(oiProblemStatusStr, OiProblemStatus.class);
            HashMap<String, ProblemStatus> contestProblems = oiProblemStatus.getContestProblems();

//            此次做题的得分
            JudgeStaticInfo judgeStaticInfo = judgeTask.getJudgeStaticInfo();
            Integer score = judgeStaticInfo.getScore();

//            如果之前没做过 更新得分
            if (!contestProblems.containsKey(problemId)) {
                contestProblems.putIfAbsent(problemId, ProblemStatus.builder()
                        .status(submissionResult)
                        ._id(_id)
                        .score(score).build());
            } else {
                ProblemStatus problemStatus = contestProblems.get(problemId);
                problemStatus.setScore(score);
                problemStatus.setStatus(submissionResult);
            }
            String jsonString = JSON.toJSONString(oiProblemStatus);
            userProfile.setOiProblemsStatus(jsonString);
        }
        userProfileMapper.updateById(userProfile);

//        更新static Info
        String problemStatisticInfo = problem.getStatisticInfo();
        HashMap<String, Integer> problemInfo = JSON.parseObject(problemStatisticInfo, new TypeReference<HashMap<String, Integer>>() {
        });
        Integer v = problemInfo.getOrDefault(String.valueOf(submissionResult), 0);
        problemInfo.put(String.valueOf(submissionResult), v + 1);

        String jsonString = JSON.toJSONString(problemInfo);
        problem.setStatisticInfo(jsonString);

//        更新提交和accept数量
        problem.setSubmissionNumber(problem.getSubmissionNumber() + 1);
        if (submissionResult.equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
            problem.setAcceptedNumber(problem.getAcceptedNumber() + 1);
        }
        problemMapper.updateById(problem);

    }

    private void updateContestRank(JudgeTask judgeTask) {
        Problem problem = judgeTask.getProblem();
        Contest contest = problem.getContest();
        String ruleType = contest.getRuleType();
        Submission submission = judgeTask.getSubmission();

        if (ruleType.equals(ContestRuleType.OI.getCode()) || contest.getRealTimeRank()) {
//            这里按照原项目的RedisKey方法进行拼接Key
            redisUtils.del(StringUtils.join(ContestConstant.CONTEST_RANK_CACHE, CommonConstant.INFIX, contest.getId()));
        }

        ContestRank rank;

        if (contest.getRuleType().equals(ContestRuleType.ACM.getCode())) {
//            AcmContestRank rank = getRank(AcmContestRank.class, submission, contest, acmContestRankMapper);
            rank = getRank(AcmContestRank.class, submission, contest, acmContestRankMapper);
        } else {
//            OiContestRankMapper rank = getRank(OiContestRankMapper.class, submission, contest, oiContestRankMapper)
            rank = getRank(OiContestRank.class, submission, contest, oiContestRankMapper);
        }
        judgeTask.setContestRank(rank);

    }

    public static <T> T getRank(Class<T> clazz, Submission submission, Contest contest, BaseMapper<T> mapper) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        qw.eq("user_id", submission.getUserId()).eq("contest_id", contest.getId());

        // 这里 mapper 是你的 MyBatis-Plus 的映射器，可以根据传入的类型来匹配
        List<T> ranks = mapper.selectList(qw);

        T userRank;
        if (Objects.isNull(ranks) || ranks.isEmpty()) {
            try {
                // 使用反射创建一个空的实例，假设你的类有无参构造函数
                userRank = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error creating a new instance of " + clazz.getName(), e);
            }
        } else {
            // 获取列表中的第一个对象
            userRank = CollUtil.getFirst(ranks);
        }

        return userRank;
    }

    private static void updateAcmContestRank(JudgeTask judgeTask) {
        ContestRank contestRank = judgeTask.getContestRank();
        String submissionInfoStr = contestRank.getSubmissionInfo();
        Problem problem = judgeTask.getProblem();
        HashMap<String, ContestSubmissionInfo> submissionInfo = JSON.parseObject(submissionInfoStr, new TypeReference<>() {
        });

        String problemId = problem.getId();
//        此题提交过
        if (submissionInfo.containsKey(problemId)) {
            ContestSubmissionInfo info = submissionInfo.get(problemId);
            if (info.getIsAc()) {
                log.info("this problem {} has been solved", problemId);
                return;
            }
            contestRank.setSubmissionNumber(contestRank.getSubmissionNumber() + 1);

//            TODO


        } else {
//           此题未提交过
        }

    }

}
