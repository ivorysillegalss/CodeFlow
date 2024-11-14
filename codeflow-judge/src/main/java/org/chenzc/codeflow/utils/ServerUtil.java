package org.chenzc.codeflow.utils;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.constant.JudgeConstant;
import org.chenzc.codeflow.domain.JudgeServer;
import org.chenzc.codeflow.mapper.JudgeServerMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ServerUtil {

    @Resource
    private JudgeServerMapper judgeServerMapper;

    @Transactional
    public JudgeServer getJudgeServer() {
        QueryWrapper<JudgeServer> qw = new QueryWrapper<>();
        qw.orderBy(true, true, "task_number");
        List<JudgeServer> judgeServers = judgeServerMapper.selectList(qw);
        if (CollUtil.isEmpty(judgeServers)) {
            log.warn("havent selected any judge server");
            return null;
        }

        for (JudgeServer server : judgeServers) {
            if (getJudgeServerStatus(server).equals(JudgeConstant.SERVER_NORMAL_STATUS)) {
                if (server.getTaskNumber() <= server.getCpuCore() * 2) {

                    server.setTaskNumber(server.getTaskNumber() + 1);
                    judgeServerMapper.updateById(server);

                    return server;
                }
            }
        }
        log.warn("havent any idle judge server");
        return null;
    }

    @Transactional
    public void afterJudgeServerGet(JudgeServer server) {
        server.setTaskNumber(server.getTaskNumber() - 1);
        judgeServerMapper.updateById(server);
    }


    private String getJudgeServerStatus(JudgeServer server) {
        Duration duration = Duration.between(LocalDateTime.now(), server.getLastHeartbeat());
        if (duration.getSeconds() > 6L) {
            return JudgeConstant.SERVER_ABNORMAL_STATUS;
        }
        return JudgeConstant.SERVER_NORMAL_STATUS;
    }
}
