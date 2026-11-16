package com.example.backend_fullstack_moviles.usuarios;

import java.util.List;
import java.util.Locale;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class UsuarioService {
    private final UsuarioRepository repository;

    UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
    List<UsuarioResumen> listar() {
        return repository.listar();
    }

    void modificar(long id, String nombre, String correo) {
        String correoNormalizado = correo.strip().toLowerCase(Locale.ROOT);
        int filas;
        try {
            filas = repository.modificar(id, nombre.strip(), correoNormalizado);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.", ex);
        }
        if (filas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
    }

    void desactivar(long id) {
        if (repository.desactivar(id) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
    }
}