package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

/**
 * @author chenz
 * @date 2024/11/19
 * @see ContestRank
 */
@Builder
@Data
@AllArgsConstructor
public class ContestSubmissionInfos {
    public HashMap<String, ContestSubmissionInfo> submissionInfos;
}