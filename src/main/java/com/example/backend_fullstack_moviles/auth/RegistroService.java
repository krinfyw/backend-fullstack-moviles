package com.example.backend_fullstack_moviles.auth;

import java.util.Locale;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
class RegistroService {
    private final UsuarioRegistroRepository repository;
    private final PasswordEncoder passwordEncoder;

    RegistroService(UsuarioRegistroRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteRegistrado registrar(String nombre, String correo, String password) {
        String nombreLimpio = nombre.strip();
        String correoNormalizado = correo.strip().toLowerCase(Locale.ROOT);
        String hash = passwordEncoder.encode(password);

        long id;
        try {
            id = repository.insertarUsuario(nombreLimpio, correoNormalizado, hash);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado.", ex);
        }

        repository.asignarClienteSonidoVivo(id);
        return new ClienteRegistrado(id, nombreLimpio, correoNormalizado, "SONIDO_VIVO", "CLIENTE");
    }
}
