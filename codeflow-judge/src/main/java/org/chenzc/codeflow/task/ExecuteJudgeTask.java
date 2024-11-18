package org.chenzc.codeflow.task;

import com.alibaba.fastjson.JSON;
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
import org.chenzc.codeflow.domain.JudgeResp;
import org.chenzc.codeflow.domain.JudgeServer;
import org.chenzc.codeflow.domain.JudgeTask;
import org.chenzc.codeflow.domain.Submission;
import org.chenzc.codeflow.entity.TaskContext;
import org.chenzc.codeflow.enums.JudgeResEnums;
import org.chenzc.codeflow.executor.TaskNodeModel;
import org.chenzc.codeflow.mapper.SubmissionMapper;
import org.chenzc.codeflow.utils.TokenUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
                if (Objects.isNull(response) || response.getStatusLine().getStatusCode() != 200) {
                    submissionMapper.updateById(submission.setResult(Integer.parseInt(JudgeResEnums.SYSTEM_ERROR.getCode())));
                    log.error("judge problem with id {} error", judgeTask.getProblem().getId());
                    return;
                }

                String stringRespBody = EntityUtils.toString(response.getEntity());
                JudgeResp judgeResp = JSON.parseObject(stringRespBody, JudgeResp.class);
                submission.setInfo(response.toString());
                taskContext.getBusinessContextData().setJudgeResp(judgeResp);

            }
        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
