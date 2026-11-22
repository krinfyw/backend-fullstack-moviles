package com.example.backend_fullstack_moviles.usuarios;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Listar, modificar y desactivar usuarios (en avance)")
@SecurityRequirement(name = "bearerAuth")
class UsuarioController {
    private final UsuarioService service;

    UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios")
    List<UsuarioResumen> listar() {
        return service.listar();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Modifica nombre y correo de un usuario")
    void modificar(@PathVariable long id, @Valid @RequestBody Modificacion datos) {
        service.modificar(id, datos.nombre(), datos.correo());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactiva un usuario")
    void desactivar(@PathVariable long id) {
        service.desactivar(id);
    }

    record Modificacion(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 150) String correo) {
    }
}