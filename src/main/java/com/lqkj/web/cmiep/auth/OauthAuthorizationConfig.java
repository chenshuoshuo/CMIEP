package com.lqkj.web.cmiep.auth;

import com.lqkj.web.cmiep.modules.manager.service.ManageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
@EnableAuthorizationServer
@EnableWebSecurity
public class OauthAuthorizationConfig extends WebSecurityConfigurerAdapter  implements AuthorizationServerConfigurer {

    @Autowired
    ManageUserService userService;

    @Autowired
    DataSource dataSource;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private WebResponseExceptionTranslator customWebResponseExceptionTranslator;

    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .allowFormAuthenticationForClients();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new LoginAuthenticationProvider(userService));
        //这东西千万不能忘
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("cmccr-h5")
                .scopes("js")
                .resourceIds("cmccr-server")
                .authorizedGrantTypes("password", "refresh_token")
                .secret(NoOpPasswordEncoder.getInstance().encode("cmccr-h5"))
                .accessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(10))
                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
                .and()
                .withClient("cmccr-h5")
                .scopes("js")
                .resourceIds("cmccr-server")
                .authorizedGrantTypes("password", "refresh_token")
                .secret(NoOpPasswordEncoder.getInstance().encode("cmccr-h5"))
                .accessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(10))
                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
                .and()
                .withClient("cmccr-guest")
                .scopes("guest")
                .resourceIds("cmccr-server")
                .authorizedGrantTypes("client_credentials")
                .secret(NoOpPasswordEncoder.getInstance().encode("cmccr-guest"))
                .accessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(365))
        ;
    }

   /* @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .userDetailsService(userService)
                .authenticationManager(authenticationManager);
    }*/

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .authenticationManager(authenticationManager)
                .userDetailsService(userService);
        if (converter != null && converter != null) {
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancerList = new ArrayList();
            enhancerList.add(converter);
            enhancerList.add(converter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
            //jwt
            endpoints.tokenEnhancer(tokenEnhancerChain)
                    .accessTokenConverter(converter);
        }
        endpoints.exceptionTranslator(customWebResponseExceptionTranslator);

    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("key/jwt.jks"), "lqkj007".toCharArray());

        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth"));

        converter.setAccessTokenConverter(new UserRulesAccessTokenConverter());

        return converter;
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(accessTokenConverter()));

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(10));
        defaultTokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7));
        return defaultTokenServices;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
