package com.lqkj.web.cmiep.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 中控发布平台权限注入逻辑
 */
public class JwtCmccrAuthenticationConverter extends JwtAuthenticationConverter {
    private final String authorities_header = "authorities";

    @Override
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> authorities = (List<String>) jwt.getClaims().get(authorities_header);

        Collection<GrantedAuthority> oldAuthorities = super.extractAuthorities(jwt);

        if (authorities == null) return oldAuthorities;

        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(v -> new SimpleGrantedAuthority("cmccr_" + v))
                .collect(Collectors.toList());

        oldAuthorities.addAll(grantedAuthorities);

        return oldAuthorities;
    }
}
