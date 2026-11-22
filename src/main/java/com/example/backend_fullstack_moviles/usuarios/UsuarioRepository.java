package com.example.backend_fullstack_moviles.usuarios;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class UsuarioRepository {
    private final JdbcTemplate jdbc;
    UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    List<UsuarioResumen> listar() {
        return jdbc.query("""
                SELECT u.id, u.nombre, u.correo, u.activo, ua.rol
                FROM usuario u
                JOIN usuario_aplicacion ua
                       ON ua.usuario_id = u.id AND ua.aplicacion = 'SONIDO_VIVO'
                ORDER BY u.id
                """,
                (rs, fila) -> new UsuarioResumen(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBoolean("activo"),
                        rs.getString("rol")));
    }
    int modificar(long id, String nombre, String correo) {
            return jdbc.update("""
            UPDATE usuario u
            SET nombre = ?, correo = ?
            WHERE u.id = ?
              AND EXISTS (
                  SELECT 1
                  FROM usuario_aplicacion ua
                  WHERE ua.usuario_id = u.id
                    AND ua.aplicacion = 'SONIDO_VIVO'
              )
            """, nombre, correo, id);
    }
    int desactivar(long id) {
        return jdbc.update("""
                UPDATE usuario u
                SET activo = FALSE
                WHERE u.id = ?
                  AND EXISTS (
                      SELECT 1
                      FROM usuario_aplicacion ua
                      WHERE ua.usuario_id = u.id
                        AND ua.aplicacion = 'SONIDO_VIVO'
                  )
                """, id);
    }
}