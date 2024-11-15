package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Builder
public class Submission {
    private String id;
    private Integer contestId;
    private Integer problemId;
    private LocalDateTime createTime;
    private Integer userId;

    private String code;


    /**
     * @see
     */
    private Integer result;

    /**
     * JSON字符串 代表用户答题情况 格式为
     * <p></p>
     * <p>
     * {"err": null,
     * "data": [{"error": 0,
     * "memory": 1695744,
     * "output": null,
     * "result": -1,
     * "signal": 0,
     * "cpu_time": 0,
     * "exit_code": 0,
     * "real_time": 3,
     * "test_case": "1",
     * "output_md5": null}
     * ]}
     */
    private String info;
    private String language;
    private Boolean shared;

    /**
     * 同为json字符串 格式为：
     * {"time_cost": 2,
     * "memory_cost": 1630208,
     * "err_info": "",
     * }
     */
    private String staticInfo;

    private String username;
    private String submissionId;
    private String ip;
}
