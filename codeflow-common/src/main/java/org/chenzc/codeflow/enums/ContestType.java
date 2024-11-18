package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContestType implements AbstractEnums{
    PUBLIC_CONTEST("1","Public"),
    PASSWORD_PROTECTED_CONTEST("2","Password Protected");
    private final String code;
    private final String message;
}
