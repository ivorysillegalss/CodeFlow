package org.chenzc.codeflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class ContestRank {
    private Integer id;
    private Integer submissionNumber;
    /**
     * acm模式下格式如下
     * @see ContestSubmissionInfo
     * Map<String, ContestSubmissionInfo>
     *  {
     *   "373": {
     *     "is_ac": false,
     *     "ac_time": 0,
     *     "is_first_ac": false,
     *     "error_number": 5
     *   },
     *   "389": {
     *     "is_ac": true,
     *     "ac_time": 4796.986907,
     *     "is_first_ac": false,
     *     "error_number": 1
     *   }
     * }
     * oi模式下为 Map<String,Integer>
     */
    private String submissionInfo;
    private Integer contestId;
    private Integer userId;
}
