package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum BusinessEnums {
    COMMIT("1", "提交补题任务"),
    JUDGE("2", "判题");
    private final String code;
    private final String message;
}
