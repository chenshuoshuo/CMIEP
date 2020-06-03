package com.lqkj.web.cmiep.auth.oauthex;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @ClassName CustomOauthException
 * @Description: TODO
 * @Author Administrator
 * @Date 2019/9/9 15:02
 * @Version V1.0
 **/
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException  extends OAuth2Exception {
    public CustomOauthException(String msg) {
        super(msg);
    }
}
