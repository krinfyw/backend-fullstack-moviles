package com.example.backend_fullstack_moviles.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Sesión", description = "Revalida el JWT contra la base de datos")
@SecurityRequirement(name = "bearerAuth")
class SesionController {
    private final LoginRepository repository;

    SesionController(LoginRepository repository) {
        this.repository = repository;
    }

    // El JWT puede seguir vigente aunque la cuenta se haya desactivado o cambiado de rol.
    // Este endpoint lo confirma contra la base para que el frontend revalide al recargar.
    @GetMapping("/api/v1/auth/sesion")
    @Operation(summary = "Devuelve la cuenta del token si sigue activa y con el mismo rol")
    SesionActual sesion(@AuthenticationPrincipal Jwt jwt) {
        long id;
        try {
            id = Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw sesionInvalida();
        }
        return repository.buscarSesion(id, jwt.getClaimAsString("rol"))
                .orElseThrow(SesionController::sesionInvalida);
    }

    private static ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida.");
    }

    record SesionActual(long id, String nombre, String correo, String rol) {
    }
}
