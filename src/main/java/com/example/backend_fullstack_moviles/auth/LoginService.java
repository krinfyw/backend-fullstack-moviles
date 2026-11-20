package com.example.backend_fullstack_moviles.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class LoginService {
    private final LoginRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Duration vigencia;

    LoginService(LoginRepository repository,
                 PasswordEncoder passwordEncoder,
                 JwtEncoder jwtEncoder,
                 @Value("${app.jwt.expiracion-minutos}") long minutos) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.vigencia = Duration.ofMinutes(minutos);
    }

    TokenRespuesta login(String correo, String password) {
        // 1. Buscar al usuario por correo y comprobar su contraseña contra el hash.
        //    Si algo falla (no existe, desactivado, clave mala) la respuesta es siempre la misma.
        var cuenta = repository.buscarPorCorreo(correo.strip().toLowerCase(Locale.ROOT))
                .filter(c -> c.activo() && passwordEncoder.matches(password, c.passwordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas."));

        // 2. Fabricar el JWT con el id del usuario, su rol y la fecha de vencimiento.
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(ahora)
                .expiresAt(ahora.plus(vigencia))
                .subject(String.valueOf(cuenta.id()))
                .claim("rol", cuenta.rol())
                .build();
        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        return new TokenRespuesta(cuenta.id(), cuenta.nombre(), cuenta.rol(), token, vigencia.toSeconds());
        }
        record TokenRespuesta(long id, String nombre, String rol, String token,
        long expiraEnSegundos) {
        }
}
