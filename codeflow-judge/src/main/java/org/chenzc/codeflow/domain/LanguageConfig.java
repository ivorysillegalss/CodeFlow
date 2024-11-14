package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LanguageConfig {
    private String name;
    private RunConfig run;
    private CompileConfig compile;
    private String template;
    private String description;
    private String content_type;
}

