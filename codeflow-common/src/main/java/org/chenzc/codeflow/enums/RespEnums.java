package org.chenzc.codeflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举类
 * @author chenz
 * @date 2024/05/29
 */
@Getter
@AllArgsConstructor
public enum RespEnums implements AbstractEnums{


    SUCCESS("1","成功"),
    FAIL("0","失败"),
    CLIENT_BAD_VARIABLES("-1","参数解析错误"),
    CLIENT_BAD_PARAMETERS("2","含接受者的参数列表为空"),

    PASSWORD_CHECK_ERROR("3","密码鉴权失败"),

    CONTEST_NOT_START("-1","比赛尚未开始"),
    CONTEST_UNDERWAY("-2","比赛筹备中");


    /**
     * 响应编码
     */
    private final String code;
    /**
     * 响应信息
     */
    private final String message;

}
