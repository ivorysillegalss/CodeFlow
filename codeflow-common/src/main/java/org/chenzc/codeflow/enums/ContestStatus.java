package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContestStatus implements AbstractEnums{
    CONTEST_NOT_START("1","未开始"),
    CONTEST_ENDED("-1","结束"),
    CONTEST_UNDERWAY("0","正在进行");
    private final String code;
    private final String message;
}
