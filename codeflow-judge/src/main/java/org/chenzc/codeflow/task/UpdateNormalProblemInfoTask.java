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
import org.chenzc.codeflow.enums.ProblemRuleType;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.ProblemMapper;
import org.chenzc.codeflow.mapper.UserMapper;
import org.chenzc.codeflow.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UpdateNormalProblemInfoTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private ProblemMapper problemMapper;

    @Resource
    private UserProfileMapper userProfileMapper;

    @Transactional
    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {
        JudgeTask judgeTask = taskContext.getBusinessContextData();
        Submission submission = judgeTask.getSubmission();

//        TODO last_result的处理 原项目此处会检查last_result是否一致 再进行更新
//            没有set过lastResult的set值 应该是在一开始读取的时候做的
        if (Objects.nonNull(submission.getLastResult())) {
            updateProblemStatusRejudge(judgeTask);
        } else {
            updateProblemStatus(judgeTask);
        }
    }

    private void updateProblemStatusRejudge(JudgeTask judgeTask) {
        Submission submission = judgeTask.getSubmission();
        Integer submissionResult = submission.getResult();
        Problem problem = getProblem(submission);
        if (Objects.isNull(problem)) {
            return;
        }

        String problemId = problem.getId();
        String lastResultStr = String.valueOf(submission.getLastResult());

        if (!lastResultStr.equals(JudgeStatus.ACCEPTED.getCode()) &&
                submissionResult.equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
            problem.setAcceptedNumber(problem.getAcceptedNumber() + 1);
            String statisticInfoStr = problem.getStatisticInfo();
            HashMap<String, Integer> problemInfo = JSON.parseObject(statisticInfoStr, new TypeReference<HashMap<String, Integer>>() {
            });

            Integer formalInfoV = problemInfo.getOrDefault(lastResultStr, 0);
            problemInfo.put(lastResultStr, formalInfoV + 1);

            String jsonString = JSON.toJSONString(problemInfo);
            problem.setStatisticInfo(jsonString);
            problemMapper.updateById(problem);

//            TODO lastResult更改 如果用户之前提交过 就需要删减staticInfo中之前所提交过的结果中的值
//             但是LaseResult的set逻辑还没加
            Integer v = problemInfo.getOrDefault(String.valueOf(submissionResult), 0);
            problemInfo.put(String.valueOf(submissionResult), v + 1);

            List<UserProfile> userProfiles = userProfileMapper.selectList(new QueryWrapper<UserProfile>().eq("id", submission.getUserId()));
            UserProfile userProfile = CollUtil.getFirst(userProfiles);
            if (problem.getRuleType().equals(ProblemRuleType.ACM.getCode())) {
                String problemsStatus = userProfile.getAcmProblemsStatus();
                AcmProblemStatus acmProblemStatus = JSON.parseObject(problemsStatus, AcmProblemStatus.class);
                HashMap<String, ProblemStatus> userProblemStatus = acmProblemStatus.getProblems();

                ProblemStatus p = userProblemStatus.get(problemId);
                if (!p.getStatus().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                    p.setStatus(submissionResult);
                    userProblemStatus.put(problemId, p);
                }
                if (String.valueOf(submissionResult).equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }
                String jsonString1 = JSON.toJSONString(acmProblemStatus);
                userProfile.setAcmProblemsStatus(jsonString1);
                userProfileMapper.updateById(userProfile);
            } else {
                String oiProblemsStatusStr = userProfile.getOiProblemsStatus();
                OiProblemStatus oiProblemStatus = JSON.parseObject(oiProblemsStatusStr, OiProblemStatus.class);

                HashMap<String, ProblemStatus> userProblemStatus = oiProblemStatus.getProblems();
                String staticInfoStr = submission.getStaticInfo();
                JudgeStaticInfo judgeStaticInfo = JSON.parseObject(staticInfoStr, JudgeStaticInfo.class);
                Integer score = judgeStaticInfo.getScore();
                ProblemStatus problemStatus = userProblemStatus.get(problemId);
                if (!problemStatus.getStatus().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                    userProfile.setTotalScore(userProfile.getTotalScore() - problemStatus.getScore() + score);
                }
//                TODO 此处原项目会再次存一次进数据库 这里先不干
                problemStatus.setStatus(submissionResult);
                problemStatus.setScore(score);
                if (String.valueOf(submissionResult).equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }

                String jsonString1 = JSON.toJSONString(userProblemStatus);
                userProfile.setOiProblemsStatus(jsonString1);
                userProfileMapper.updateById(userProfile);
            }
        }

    }

    private void updateProblemStatus(JudgeTask judgeTask) {
        Submission submission = judgeTask.getSubmission();
        Integer submissionResult = submission.getResult();
        String submissionResultStr = String.valueOf(submissionResult);

//        在提交的过程中有可能 问题的数据被其他线程改变 但是不会改变的是问题的id
        String problemId = judgeTask.getProblem().getId();
//        更新题目信息
        Problem problem = getProblem(submission);
        if (Objects.isNull(problem)) {
            return;
        }
        problem.setSubmissionNumber(problem.getSubmissionNumber() + 1);
        if (submissionResultStr.equals(JudgeStatus.ACCEPTED.getCode())) {
            problem.setAcceptedNumber(problem.getAcceptedNumber() + 1);
        }

        String statisticInfoJson = problem.getStatisticInfo();
        Map<String, Integer> problemInfo = JSON.parseObject(statisticInfoJson, new TypeReference<Map<String, Integer>>() {
        });

        Integer problemFormalV = problemInfo.getOrDefault(submissionResultStr, 0);
        problemInfo.put(submissionResultStr, problemFormalV + 1);
        String jsonString = JSON.toJSONString(problemInfo);
        problem.setStatisticInfo(jsonString);
        problemMapper.updateById(problem);

//        更新用户个人信息
        List<UserProfile> userProfiles = userProfileMapper.selectList(new QueryWrapper<UserProfile>().eq("id", submission.getUserId()));
        UserProfile userProfile = CollUtil.getFirst(userProfiles);
        userProfile.setSubmissionNumber(userProfile.getSubmissionNumber() + 1);
        if (problem.getRuleType().equals(ProblemRuleType.ACM.getCode())) {
            String problemsStatusStr = userProfile.getAcmProblemsStatus();
            HashMap<String, ProblemStatus> problemsStatus = JSON.parseObject(problemsStatusStr, AcmProblemStatus.class).getProblems();
            if (!problemsStatus.containsKey(problemId)) {
                problemsStatus.put(problemId, ProblemStatus.builder()
                        .status(submissionResult)
                        ._id(problem.get_id()).build());
                if (submissionResultStr.equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }
            } else if (!String.valueOf(problemsStatus.get(problemId).getStatus())
                    .equals(JudgeStatus.ACCEPTED.getCode())) {
                problemsStatus.get(problemId).setStatus(submissionResult);
                if (submissionResultStr.equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }
            }
            String jsonString1 = JSON.toJSONString(problemsStatus);
            userProfile.setAcmProblemsStatus(jsonString1);
            userProfileMapper.updateById(userProfile);
        } else {
            String oiProblemsStatusStr = userProfile.getOiProblemsStatus();
            HashMap<String, ProblemStatus> oiProblemsStatus = JSON.parseObject(oiProblemsStatusStr, OiProblemStatus.class).getProblems();
            String staticInfo = submission.getStaticInfo();
            Integer score = JSON.parseObject(staticInfo, JudgeStaticInfo.class).getScore();
            if (!oiProblemsStatus.containsKey(problemId)) {
                userProfile.setTotalScore(userProfile.getTotalScore() + score);
                oiProblemsStatus.put(problemId, ProblemStatus.builder()
                        .status(submissionResult)
                        .score(score)
                        ._id(problem.get_id()).build());
                if (submissionResultStr.equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }
            } else if (String.valueOf(oiProblemsStatus.get(problemId).getStatus()).equals(JudgeStatus.ACCEPTED.getCode())) {
                int updatedScore = userProfile.getTotalScore() - oiProblemsStatus.get(problemId).getScore() + score;
                userProfile.setTotalScore(updatedScore);
                oiProblemsStatus.get(problemId).setScore(score);
                oiProblemsStatus.get(problemId).setStatus(submissionResult);
                if (submissionResultStr.equals(JudgeStatus.ACCEPTED.getCode())) {
                    userProfile.setAcceptedNumber(userProfile.getAcceptedNumber() + 1);
                }
            }

            String jsonString1 = JSON.toJSONString(oiProblemsStatus);
            userProfile.setOiProblemsStatus(jsonString1);
            userProfileMapper.updateById(userProfile);

        }
    }

    private Problem getProblem(Submission submission) {
        List<Problem> problems = problemMapper.selectList(new QueryWrapper<Problem>()
                .eq("contest_id", submission.getContestId())
                .eq("problem_id", submission.getProblemId()));
        if (CollUtil.isEmpty(problems)) {
            log.error("cannot find target problem with id {}", submission.getProblemId());
            return null;
        }
        return CollUtil.getFirst(problems);
    }
}
