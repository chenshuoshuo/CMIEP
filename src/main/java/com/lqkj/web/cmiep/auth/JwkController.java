package com.lqkj.web.cmiep.auth;

import com.lqkj.web.cmiep.APIVersion;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.security.KeyStore;

@Api(tags = "oauth证书服务")
@RestController
public class JwkController {

    @ApiOperation("获取证书")
    @GetMapping("/jwk/" + APIVersion.V1 + "/keys")
    public ResponseEntity<String> jws() throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(jwkString());
    }

    private String jwkString() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        ClassPathResource classPathResource = new ClassPathResource("key/jwt.jks");

        InputStream inputStream = classPathResource.getInputStream();

        try {
            keyStore.load(inputStream, "lqkj007".toCharArray());

            JWK jwk = JWK.load(keyStore, "oauth", "lqkj007".toCharArray());

            JWKSet jwkSet = new JWKSet(jwk.toPublicJWK());

            return jwkSet.toString();
        } finally {
            inputStream.close();
        }
    }
}
