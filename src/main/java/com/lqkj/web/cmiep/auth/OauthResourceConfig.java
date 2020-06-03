package com.lqkj.web.cmiep.auth;

import com.lqkj.web.cmiep.APIVersion;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * <p>
 * http://192.168.4.105:8100/cmccr/oauth/token?grant_type=client_credentials&client_id=demo&client_secret=demo
 * </p>
 * <p>
 * http://192.168.4.105:8100/cmccr/oauth/authorize?response_type=token&client_id=demo&client_secret=demo&scope=demo&redirect_uri=http://baidu.com
 * </p>
 * <p>
 * http://192.168.4.105:8100/cmccr/oauth/token?client_id=cmccr-h5&client_secret=cmccr-h5&grant_type=password&username=free&password=123456
 * </p>
 * <p>
 * http://192.168.4.105:8100/cmccr/oauth/token?grant_type=refresh_token&refresh_token=fbde81ee-f419-42b1-1234-9191f1f95be9&client_id=cmccr-h5&client_secret=cmccr-h5
 * </p>
 * 2018001004/123456
 */
@Configuration
@EnableResourceServer
public class OauthResourceConfig extends ResourceServerConfigurerAdapter implements ResourceServerConfigurer   {

    @Autowired
    TokenStore tokenStore;

    @Autowired
    ResourceServerTokenServices tokenService;

    @Autowired
    Environment environment;



    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("cmccr-server")
                .tokenStore(tokenStore)
                .tokenServices(tokenService)
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/center/user/register",
                        "/center/cas/*",
                        "/socket/**")
                .permitAll()
                .antMatchers(
                        "/jwk/v1/keys") // CMGIS推送更新变更的接口
                .permitAll()
                .antMatchers(HttpMethod.OPTIONS) // 允许OPTIONS跨域验证请求通过
                .permitAll()
                .antMatchers(
                        "/center/user/**"
                )
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwkSetUri(getLocalJwkUrl())
                .jwtAuthenticationConverter(new JwtCmccrAuthenticationConverter())
                .and()
                .and()
                //关闭iframe跨域
                .headers().frameOptions().disable()
        ;
    }


    private String getLocalJwkUrl() {
        String jwkUrl = environment.getProperty("oauth2.jwk-url");

        if (StringUtils.isNotEmpty(jwkUrl)) return jwkUrl;

        return new StringBuilder().append("http://")
                .append(environment.getProperty("server.address")).append(":")
                .append(environment.getProperty("server.port"))
                .append(environment.getProperty("server.servlet.context-path"))
                .append("/jwk/").append(APIVersion.V1).append("/keys").toString();
    }
}
