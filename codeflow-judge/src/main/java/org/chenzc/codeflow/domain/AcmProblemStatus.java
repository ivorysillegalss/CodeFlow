package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Builder
@Data
@AllArgsConstructor
public class AcmProblemStatus {
    private HashMap<String, ProblemStatus> contestProblems;
}
