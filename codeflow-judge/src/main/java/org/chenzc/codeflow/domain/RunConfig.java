package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RunConfig {
    public List<String> env;
    public String command;
    public Object seccomp_rule;  // It can be a String or a Map depending on the content.
    public Integer memory_limit_check_only;
}
