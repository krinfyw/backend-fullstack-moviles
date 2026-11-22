package com.example.backend_fullstack_moviles.auth;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class LoginRepository {
    private final JdbcTemplate jdbc;

    LoginRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    record Cuenta(long id, String nombre, String passwordHash, boolean activo, String rol) {
    }

    Optional<Cuenta> buscarPorCorreo(String correo, String aplicacion) {
        return jdbc.query("""
                SELECT u.id, u.nombre, u.password_hash, u.activo, ua.rol
                FROM usuario u
                JOIN usuario_aplicacion ua
                ON ua.usuario_id = u.id AND ua.aplicacion = ?
                WHERE u.correo = ?
                """,
                (rs, fila) -> new Cuenta(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("password_hash"),
                        rs.getBoolean("activo"),
                        rs.getString("rol")),
                correo).stream().findFirst();
    }
}
