package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Template {
    private String prepend;
    private String template;
    private String append;
}
