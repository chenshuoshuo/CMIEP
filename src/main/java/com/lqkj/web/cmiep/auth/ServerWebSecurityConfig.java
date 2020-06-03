package com.lqkj.web.cmiep.auth;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Order(1)
@Configuration
@EnableWebSecurity
public class ServerWebSecurityConfig  extends WebSecurityConfigurerAdapter {
    private static final String SWAGGER_URL = "/swagger-ui.html";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.requestMatcher(new LuminaryRequestedMatcher())
                .httpBasic()
                .and()
                .authorizeRequests()
                // swagger页面需要添加登录校验
                .antMatchers(SWAGGER_URL).authenticated()
                // 监控节点需要添加登录校验
                .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated();

    }

    private static class LuminaryRequestedMatcher implements RequestMatcher {
        public boolean matches(HttpServletRequest request) {
            AntPathRequestMatcher swaggerRequestMatcher = new AntPathRequestMatcher(SWAGGER_URL);
            EndpointRequest.EndpointRequestMatcher endpointRequestMatcher = EndpointRequest.toAnyEndpoint();
            return swaggerRequestMatcher.matches(request) || endpointRequestMatcher.matches(request);
        }
    }

}
