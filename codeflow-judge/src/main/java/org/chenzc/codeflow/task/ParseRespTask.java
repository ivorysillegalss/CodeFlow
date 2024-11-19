package org.chenzc.codeflow.task;

import com.alibaba.fastjson.JSON;
import com.chenzc.codeflow.enums.JudgeStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.ProblemRuleType;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ParseRespTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private SubmissionMapper submissionMapper;

    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {
        JudgeTask judgeTask = taskContext.getBusinessContextData();
        Submission submission = judgeTask.getSubmission();
        JudgeResp judgeResp = judgeTask.getJudgeResp();

        JudgeStaticInfo judgeStaticInfo;

        if (Objects.nonNull(judgeResp.getError())) {
            submission.setResult(Integer.parseInt(JudgeStatus.COMPILE_ERROR.getCode()));
            submissionMapper.updateById(submission);
            judgeStaticInfo = JudgeStaticInfo.builder().errInfo(judgeResp.getData())
                    .score(JudgeConstant.JUDGE_ZERO_SCORE).build();
        } else {

            //                    根据TestCase中的值进行排序
            List<JudgeRespData> data = judgeResp.getData();
            data.sort((s1, s2) -> Integer.compare(Integer.parseInt(s1.getTestCase()),
                    Integer.parseInt(s2.getTestCase())));

            judgeStaticInfo = computeStaticInfo(data, submission, judgeTask.getProblem());

            List<Integer> errorCaseIndexes = getErrorCaseIndex(data);
            if (errorCaseIndexes.isEmpty()) {
                submission.setResult(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()));
            } else if (judgeTask.getProblem().getRuleType().equals(ProblemRuleType.ACM.getCode())
                    || errorCaseIndexes.size() == data.size()
            ) {
                submission.setResult(data.get(errorCaseIndexes.get(0)).getResult());
            } else {
                submission.setResult(Integer.parseInt(JudgeStatus.PARTIALLY_ACCEPTED.getCode()));
            }
        }

        judgeTask.setJudgeStaticInfo(judgeStaticInfo);
        String jsonStaticInfo = JSON.toJSONString(judgeStaticInfo);
        submission.setStaticInfo(jsonStaticInfo);

        submissionMapper.updateById(submission);
    }

    private List<Integer> getErrorCaseIndex(List<JudgeRespData> data) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).getResult().equals(0)) {
                res.add(i);
            }
        }
        return res;
    }

    private JudgeStaticInfo computeStaticInfo(List<JudgeRespData> data, Submission submission, Problem problem) {
        int maxCpuTime = 0;
        int maxMemoryCost = 0;
        for (JudgeRespData d : data) {
            maxCpuTime = Math.max(d.getCpuTime(), maxCpuTime);
            maxMemoryCost = Math.max(d.getMemory(), maxMemoryCost);
        }
        Integer score = 0;
        if (problem.getRuleType().equals(ProblemRuleType.OI.getCode())) {
            String testCaseScore = problem.getTestCaseScore();
            List<JudgeTestCase> testCases = JSON.parseArray(testCaseScore, JudgeTestCase.class);

            try {
                for (int i = 0; i < data.size(); i++) {
                    JudgeRespData d = data.get(i);
                    if (d.getResult().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
                        score += testCases.get(i).getScore();
                    } else {
                        d.setScore(0);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                log.error("Index Error raised when summing up the score in problem,id : {}", problem.getId());
                score = 0;
            }
        }
        return JudgeStaticInfo.builder()
                .score(score)
                .memoryCost(maxMemoryCost)
                .timeCost(maxCpuTime)
                .build();
    }

}
