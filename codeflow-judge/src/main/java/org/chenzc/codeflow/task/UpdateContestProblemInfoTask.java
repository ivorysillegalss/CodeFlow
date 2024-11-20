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
import org.chenzc.codeflow.constant.JudgeConstant;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UpdateContestProblemInfoTask implements TaskNodeModel<JudgeTask> {

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
            rank = getRank(AcmContestRank.class, submission, contest, acmContestRankMapper);
        } else {
            rank = getRank(OiContestRank.class, submission, contest, oiContestRankMapper);
        }
        judgeTask.setContestRank(rank);
        updateContestRank(judgeTask, acmContestRankMapper, oiContestRankMapper);
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


    //        调用这个地方的方法 应该更新problem的信息 应为会存在并发等问题
    private static void updateContestRank(JudgeTask judgeTask, AcmContestRankMapper acmContestRankMapper, OiContestRankMapper oiContestRankMapper) {
        ContestRank contestRank = judgeTask.getContestRank();
        Submission submission = judgeTask.getSubmission();
        Problem problem = judgeTask.getProblem();
        Contest contest = problem.getContest();
        Integer submissionResult = submission.getResult();
        ContestSubmissionInfo info;
        String problemId = problem.getId();
        String submissionInfoStr = contestRank.getSubmissionInfo();

        if (contest.getRuleType().equals(ContestRuleType.ACM.getCode())) {
            HashMap<String, ContestSubmissionInfo> submissionInfo = JSON.parseObject(submissionInfoStr, new TypeReference<>() {
            });
            AcmContestRank rank = (AcmContestRank) contestRank;

            //        此题提交过
            if (submissionInfo.containsKey(problemId)) {
                info = submissionInfo.get(problemId);
                if (info.getIsAc()) {
                    log.info("this problem {} has been solved", problemId);
                    return;
                }
                contestRank.setSubmissionNumber(contestRank.getSubmissionNumber() + 1);

                if (submissionResult.equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                    double v = Duration.between(submission.getCreateTime(), contest.getStartTime()).toNanos() / 1_000_000_000.0;
                    info.setIsAc(Boolean.TRUE)
                            .setAcTime((float) (v));
                    rank.setAcceptedNumber(rank.getAcceptedNumber() + 1)
                            .setTotalTime(info.getAcTime() + info.getErrorNumber() * 20 * 60);

//                    在updateContestProblemStatus此函数处 已对problem的AC数量做了更改
                    if (problem.getAcceptedNumber().equals(JudgeConstant.FIRST_AC)) {
                        info.setIsFirstAc(Boolean.TRUE);
                    }

                } else if (!submissionResult.equals(Integer.parseInt(JudgeStatus.COMPILE_ERROR.getCode()))) {
                    info.setErrorNumber(info.getErrorNumber() + 1);
                }


            } else {
//           此题未提交过 用户第一次提交
                contestRank.setSubmissionNumber(contestRank.getSubmissionNumber() + 1);
                info = ContestSubmissionInfo.builder()
                        .isAc(Boolean.FALSE)
                        .isFirstAc(Boolean.FALSE).build();

                if (submissionResult.equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {

                    rank.setAcceptedNumber(rank.getAcceptedNumber() + 1);

                    info.setIsAc(Boolean.TRUE)
                            .setAcTime(info.getAcTime() + info.getErrorNumber() * 20 * 60);
                    rank.setTotalTime(info.getAcTime() + rank.getTotalTime());

//                    在updateContestProblemStatus此函数处 已对problem的AC数量做了更改
                    if (problem.getAcceptedNumber().equals(JudgeConstant.FIRST_AC)) {
                        info.setIsFirstAc(Boolean.TRUE);
                    }

                } else if (!submissionResult.equals(Integer.parseInt(JudgeStatus.COMPILE_ERROR.getCode()))) {
                    info.setErrorNumber(info.getErrorNumber() + 1);
                }
            }
            Integer updateId = submission.getProblemId();
            submissionInfo.put(String.valueOf(updateId), info);
            String jsonString = JSON.toJSONString(submissionInfo);
            contestRank.setSubmissionInfo(jsonString);

            acmContestRankMapper.updateById(rank);

        } else {
            OiContestRank rank = (OiContestRank) contestRank;

            JudgeStaticInfo judgeStaticInfo = judgeTask.getJudgeStaticInfo();
            Integer currentScore = judgeStaticInfo.getScore();

            String submissionInfo = rank.getSubmissionInfo();
            Map<String, Integer> infoMap = JSON.parseObject(submissionInfo, new TypeReference<>() {
            });
            Integer lastScore = infoMap.get(problemId);

//        TODO lastScore的赋值 好像并没有 三方类代替下方判断
            if (!lastScore.equals(CommonConstant.FALSE)) {
                rank.setTotalScore(rank.getTotalScore() - lastScore + currentScore);
            } else {
                rank.setTotalScore(rank.getTotalScore() + currentScore);
            }
            infoMap.put(problemId, currentScore);
            String jsonString = JSON.toJSONString(infoMap);
            rank.setSubmissionInfo(jsonString);
            oiContestRankMapper.updateById(rank);
        }
    }
}
