package com.example.backend_fullstack_moviles.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {
    private final LoginService service;

    LoginController(LoginService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/auth/login")
    LoginService.TokenRespuesta login(@Valid @RequestBody Solicitud solicitud) {
        return service.login(solicitud.correo(), solicitud.password());
    }

    record Solicitud(
            @NotBlank @Email @Size(max = 150) String correo,
            @NotBlank @Size(max = 128) String password) {
    }
}
