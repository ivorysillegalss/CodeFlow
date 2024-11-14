package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompileConfig {
    private String exe_name;
    private String src_name;
    private Long max_memory;
    private Long max_cpu_time;
    private Long max_real_time;
    private String compile_command;
}

