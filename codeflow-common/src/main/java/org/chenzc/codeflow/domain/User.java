package org.chenzc.codeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String username;
    private String password;
    private OffsetDateTime lastLogin;
    private String email;
    private OffsetDateTime createTime;
    private String adminType;
    private String resetPasswordToken;
    private OffsetDateTime resetPasswordTokenExpireTime;
    private String authToken;
    private Boolean twoFactorAuth;
    private String tfaToken;
    private Boolean openApi;
    private String openApiAppkey;
    private Boolean isDisabled;
    private String problemPermission;
    /**
     * json字符串
     */
    private String sessionKeys;
}
