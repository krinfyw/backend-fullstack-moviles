package com.example.backend_fullstack_moviles.auth;

import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
class UsuarioRegistroRepository {
    private final JdbcTemplate jdbc;

    UsuarioRegistroRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    long insertarUsuario(String nombre, String correo, String hash) {
        var claves = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var sql = connection.prepareStatement(
                    "INSERT INTO usuario (nombre, correo, password_hash) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sql.setString(1, nombre);
            sql.setString(2, correo);
            sql.setString(3, hash);
            return sql;
        }, claves);

        Number id = claves.getKey();
        if (id == null) {
            throw new IllegalStateException("MySQL no generó el ID del usuario.");
        }
        return id.longValue();
    }

    void asignarClienteSonidoVivo(long usuarioId) {
        jdbc.update("""
                INSERT INTO usuario_aplicacion (usuario_id, aplicacion, rol)
                VALUES (?, 'SONIDO_VIVO', 'CLIENTE')
                """, usuarioId);
    }
}
