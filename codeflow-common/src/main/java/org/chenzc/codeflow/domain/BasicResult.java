package org.chenzc.codeflow.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chenz
 * @date 2024/11/12
 */
@Builder
@Data
@Accessors(chain = true)
public class BasicResult {
    private String error;
    private Object data;


    public static BasicResult success(Object data) {
        return BasicResult.builder().data(data).build();
    }

    public static BasicResult success() {
        return BasicResult.builder().build();
    }

    public static BasicResult fail() {
        return BasicResult.builder().build();
    }

    public static BasicResult fail(String error) {
        return BasicResult.builder().data("error").error(error).build();
    }

    public static BasicResult fail(Object data, String error) {
        return BasicResult.builder().data(data).error(error).build();
    }
}
