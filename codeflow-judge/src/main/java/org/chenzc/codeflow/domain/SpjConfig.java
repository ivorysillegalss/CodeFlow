package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpjConfig {
    private String command;
    private String exe_name;
    private String seccomp_rule;
}
