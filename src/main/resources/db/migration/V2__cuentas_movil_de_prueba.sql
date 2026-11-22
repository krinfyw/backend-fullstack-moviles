INSERT IGNORE INTO usuario (nombre, correo, password_hash, activo) VALUES
    ('Docente LapiLearn',    'docente@duoc.cl',
     '$argon2id$v=19$m=19456,t=2,p=1$/SYPCk0IpAjuqz8thCuvKQ$fvhH6IUQpNePLlkiHBXdJSQHJRKUUqXAXquDiVTScKY', TRUE),
    ('Asistente LapiLearn', 'asistente@duoc.cl',
     '$argon2id$v=19$m=19456,t=2,p=1$/SYPCk0IpAjuqz8thCuvKQ$fvhH6IUQpNePLlkiHBXdJSQHJRKUUqXAXquDiVTScKY', TRUE);

INSERT IGNORE INTO usuario_aplicacion (usuario_id, aplicacion, rol)
SELECT id, 'MOVIL', 'DOCENTE' FROM usuario WHERE correo = 'docente@duoc.cl';

INSERT IGNORE INTO usuario_aplicacion (usuario_id, aplicacion, rol)
SELECT id, 'MOVIL', 'ASISTENTE' FROM usuario WHERE correo = 'asistente@duoc.cl';