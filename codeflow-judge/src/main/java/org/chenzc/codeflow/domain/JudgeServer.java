package org.chenzc.codeflow.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JudgeServer {
    private Integer id;
    private String hostname;
    private String ip;
    private String judgerVersion;
    private Integer cpuCore;
    private Float memoryUsage;
    private Float cpuUsage;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime createTime;
    private Integer taskNumber;
    private String serviceUrl;
    private Boolean isDisabled;
}
