package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JudgeResEnums implements AbstractEnums {
    COMPILE_ERROR("-2", "Compile Error"),
    WRONG_ANSWER("-1", "Wrong Answer"),
    ACCEPTED("0", "Accepted"),
    CPU_TIME_LIMIT_EXCEEDED("1", "CPU Time Limit Exceeded"),
    REAL_TIME_LIMIT_EXCEEDED("2", "Real Time Limit Exceeded"),
    MEMORY_LIMIT_EXCEEDED("3", "Memory Limit Exceeded"),
    RUNTIME_ERROR("4", "Runtime Error"),
    SYSTEM_ERROR("5", "System Error"),
    PENDING("6", "Pending"),
    JUDGING("7", "Judging"),
    PARTIALLY_ACCEPTED("8", "Partially Accepted");
    private final String code;
    private final String message;
}
