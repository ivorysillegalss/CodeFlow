package com.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.chenzc.codeflow.enums.AbstractEnums;

@Getter
@AllArgsConstructor
public enum JudgeStatus implements AbstractEnums {
    COMPILE_ERROR("-2", "编译错误"),
    WRONG_ANSWER("-1", "错误答案"),
    ACCEPTED("0", "通过"),
    CPU_TIME_LIMIT_EXCEEDED("1", "CPU时间超限"),
    REAL_TIME_LIMIT_EXCEEDED("2", "实际时间超限"),
    MEMORY_LIMIT_EXCEEDED("3", "内存超限"),
    RUNTIME_ERROR("4", "运行时错误"),
    SYSTEM_ERROR("5", "系统错误"),
    PENDING("6", "等待中"),
    JUDGING("7", "评判中"),
    PARTIALLY_ACCEPTED("8", "部分通过");

    private final String code;
    private final String message;
}

