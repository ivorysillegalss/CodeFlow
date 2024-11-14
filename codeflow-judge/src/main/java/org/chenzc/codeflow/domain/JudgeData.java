package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JudgeData {
    private String languageConfig;
    private String src;
    private Integer maxCpuTime;
    private Integer maxMemory;
    private String testCaseId;
    private Boolean output;
    private String spjVersion;
    private SpjConfig spjConfig;
    private CompileConfig spjCompileConfig;
    private String spjSrc;
    private String ioMode;
}
