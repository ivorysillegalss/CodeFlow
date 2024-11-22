package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

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
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private OffsetDateTime createTime;
    private OffsetDateTime lastUpdateTime;
    private Boolean visible;
    private Integer createdById;
    private String allowedIpRanges;
    private String contestType;
    private String status;
}
