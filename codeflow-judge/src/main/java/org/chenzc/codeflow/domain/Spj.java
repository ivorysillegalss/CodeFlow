package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Spj {
    private SpjConfig config;
    private CompileConfig compile;
}

