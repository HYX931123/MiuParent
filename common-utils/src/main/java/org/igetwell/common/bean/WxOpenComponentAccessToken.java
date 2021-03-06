package org.igetwell.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取第三方平台access_token
 */
@Getter
@Setter
public class WxOpenComponentAccessToken {

    /**
     * 第三方平台access_token
     */
    private String componentAccessToken;


    /**
     * 有效期
     */
    private long expiresIn = -1;
}
