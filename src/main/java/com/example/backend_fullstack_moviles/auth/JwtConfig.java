package com.example.backend_fullstack_moviles.auth;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
class JwtConfig {
    @Bean
    JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secreto) {
        var llave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(llave));
    }
    @Bean
    JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secreto) {
        var llave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(llave)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }
}
