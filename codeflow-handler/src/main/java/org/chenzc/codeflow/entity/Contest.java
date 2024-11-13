package org.chenzc.codeflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Contest {
    private Integer id;
    private String title;
    private String description;
    private Boolean realTimeRank;
    private String password;
    private String ruleType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    private Boolean visible;
    private Integer createdById;
    private String allowedIpRanges;
//    TODO
    private String contestType;
    private String status;
}
