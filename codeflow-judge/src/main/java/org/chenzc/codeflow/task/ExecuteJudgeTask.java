package org.chenzc.codeflow.task;

import com.alibaba.fastjson.JSON;
import com.chenzc.codeflow.enums.JudgeStatus;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.*;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.JudgeResEnums;
import org.chenzc.codeflow.enums.ProblemRuleType;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.SubmissionMapper;
import org.chenzc.codeflow.utils.TokenUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ExecuteJudgeTask implements TaskNodeModel<JudgeTask> {

    @Resource
    private SubmissionMapper submissionMapper;

    @SneakyThrows
    @Override
    public void execute(TaskContext<JudgeTask> taskContext) {
        JudgeTask judgeTask = taskContext.getBusinessContextData();
        JudgeServer judgeServer = judgeTask.getJudgeServer();
        Submission submission = judgeTask.getSubmission();

        String judgeDataStr = JSON.toJSONString(judgeTask.getJudgeData());
        String judgeUrl = StringUtils.join(judgeServer.getServiceUrl(), JudgeConstant.JUDGE_SERVICE_ENDPOINT);

        String judgeServerToken = TokenUtil.generateToken(JudgeConstant.JUDGE_SERVER_TOKEN);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000) // 连接超时 5 秒
                .setSocketTimeout(5000)  // 读取超时 5 秒
                .build();

        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpPost post = new HttpPost(judgeUrl);
            post.setHeader("X-Judge-Server-Token", judgeServerToken);
            post.setEntity(new StringEntity(judgeDataStr));

            try (CloseableHttpResponse response = client.execute(post)) {
                if (Objects.isNull(response)) {
                    submissionMapper.updateById(submission.setResult(Integer.parseInt(JudgeResEnums.SYSTEM_ERROR.getCode())));
//                    TODO
                }

                String stringRespBody = EntityUtils.toString(response.getEntity());
                JudgeResp judgeResp = JSON.parseObject(stringRespBody, JudgeResp.class);

                JudgeStaticInfo judgeStaticInfo;

                if (Objects.nonNull(judgeResp.getError())) {
                    submission.setResult(Integer.parseInt(JudgeStatus.COMPILE_ERROR.getCode()));
                    judgeStaticInfo = JudgeStaticInfo.builder().errInfo(judgeResp.getData()).score(JudgeConstant.JUDGE_ZERO_SCORE).build();
                } else {
//                    根据TestCase中的值进行排序
                    List<JudgeRespData> data = judgeResp.getData();
                    data.sort((s1, s2) -> Integer.compare(Integer.parseInt(s1.getTestCase()),
                            Integer.parseInt(s2.getTestCase())));
                    submission.setInfo(response.toString());

                    judgeStaticInfo = computeStaticInfo(data, submission, judgeTask.getProblem());

                    List<Integer> errorCaseIndexes = getErrorCaseIndex(data);
                    if (errorCaseIndexes.isEmpty()) {
                        submission.setResult(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()));
                    } else if (judgeTask.getProblem().getRuleType().equals(ProblemRuleType.ACM.getCode())
                            || errorCaseIndexes.size() == data.size()
                    ) {
                        submission.setResult(data.get(errorCaseIndexes.get(0)).getResult());
                    }else {
                        submission.setResult(Integer.parseInt(JudgeStatus.PARTIALLY_ACCEPTED.getCode()));
                    }
                }
                String jsonStaticInfo = JSON.toJSONString(judgeStaticInfo);
                submission.setStaticInfo(jsonStaticInfo);

                submissionMapper.updateById(submission);

                if (response.getStatusLine().getStatusCode() == 200) {
//                    TODO
                    System.out.println("Response: " + response.getEntity());
                } else {
//                    TODO
                    System.out.println("Request failed with status code: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
            e.printStackTrace();
        }
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
//            JSON.parseArray()

            try {
                for (int i = 0; i < data.size(); i++) {
                    JudgeRespData d = data.get(i);

                    if (d.getResult().equals(Integer.parseInt(JudgeStatus.ACCEPTED.getCode()))) {
//                        TODO 同理json字符串 获取这里的对应第i个样例的score 赋值累加
//                         score
                    } else {
                        d.setScore(0);
                    }

                }
            } catch (IndexOutOfBoundsException e) {
                log.error(StringUtils.join("Index Error raised when summing up the score in problem "),
                        problem.getId());
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
