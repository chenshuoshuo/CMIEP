package com.lqkj.web.cmiep.config;

import com.google.common.collect.Lists;
import com.lqkj.web.cmiep.APIVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.List;

/**
 * api文档配置
 */
@Configuration
@EnableSwagger2
public class Swagger2Configure {

    @Autowired
    Environment environment;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(Lists.newArrayList(securityScheme()))
                .securityContexts(Lists.newArrayList(securityContext()))
                .directModelSubstitute(Timestamp.class, String.class)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lqkj.web.cmiep"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("可视化校庆平台")
                .description("可视化校庆平台api")
                .termsOfServiceUrl("http://www.you07.com")
                .version(APIVersion.V1)
                .build();
    }

    /**
     * 授权方式
     */
    private SecurityScheme securityScheme() {
        List<GrantType> grantTypes = Lists.newArrayList(
                new ResourceOwnerPasswordCredentialsGrant(environment.getProperty("server.servlet.context-path") +
                        "/oauth/token")
        );

        return new OAuthBuilder()
                .grantTypes(grantTypes)
                .name("oauth2")
                .scopes(Lists.newArrayList(new AuthorizationScope("js", "web-js")))
                .build();
    }

    /**
     * 授权信息
     */
    private SecurityContext securityContext() {
        SecurityReference osmSecurityReference = new SecurityReference("oauth2",
                new AuthorizationScope[]{});

        return SecurityContext.builder()
                .forPaths(PathSelectors.any())
                .securityReferences(Lists.newArrayList(osmSecurityReference))
                .build();
    }
}
