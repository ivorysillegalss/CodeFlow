package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContestRuleType implements AbstractEnums {
    ACM("1", "ACM"),
    OI("2", "OI");
    private final String code;
    private final String message;
}
