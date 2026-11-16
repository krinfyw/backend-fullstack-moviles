package com.example.backend_fullstack_moviles.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RegistroController {
    private final RegistroService service;

    RegistroController(RegistroService service) {
        this.service = service;
    }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    ClienteRegistrado registrar(@Valid @RequestBody Solicitud solicitud) {
        return service.registrar(solicitud.nombre(), solicitud.correo(), solicitud.password());
    }

    record Solicitud(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 150) String correo,
            @NotBlank @Size(min = 12, max = 128) String password) {
    }
}
