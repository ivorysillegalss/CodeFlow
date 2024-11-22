package org.chenzc.codeflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TaskEnums {

    SUCCESS("0","操作成功"),
    FAIL("-1","操作失败"),
    EXECUTING("100","责任链执行中");


    private final String code;
    private final String msg;

}
