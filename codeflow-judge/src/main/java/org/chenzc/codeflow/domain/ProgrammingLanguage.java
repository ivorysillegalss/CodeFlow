package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgrammingLanguage {
    private Spj spj;
    private String name;
    private LanguageConfig config;
}
