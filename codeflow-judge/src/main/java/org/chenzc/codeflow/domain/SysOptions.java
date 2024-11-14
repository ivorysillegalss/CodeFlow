package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SysOptions {
    private Integer id;
    private String key;
    private String value;
}
