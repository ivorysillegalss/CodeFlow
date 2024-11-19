package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Builder
@Data
@AllArgsConstructor
public class OiProblemStatus {
    private HashMap<String, ProblemStatus> contestProblems;
    private HashMap<String, ProblemStatus> problems;
}
