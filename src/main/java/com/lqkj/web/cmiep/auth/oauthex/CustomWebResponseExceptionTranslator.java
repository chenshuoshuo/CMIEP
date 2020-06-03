package com.lqkj.web.cmiep.auth.oauthex;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * @ClassName CustomWebResponseExceptionTranslator
 * @Description: TODO
 * @Author Administrator
 * @Date 2019/9/9 15:19
 * @Version V1.0
 **/
@Component("customWebResponseExceptionTranslator")
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        try {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
            return ResponseEntity
                    .status(200)
                    .body(new CustomOauthException("用户或密码错误"));
        }catch (Exception en){
            return ResponseEntity
                    .status(200)
                    .body(new CustomOauthException("用户或密码错误"));
        }

    }
}