# Sonido Vivo · API REST

Proyecto semestral de Desarrollo Fullstack II. Equipo: Santiago Matheo, Vicente Aninao y Martín Pérez. [Frontend React](https://github.com/martin-perezr/audio-vivo-react).

La API de la tienda usa Java 21, Spring Boot en EC2 y MySQL en RDS. JdbcTemplate ejecuta SQL con parámetros, Flyway versiona el esquema y springdoc genera OpenAPI y Swagger UI. El alcance de este avance es el registro, el login de Sonido Vivo y el inicio de la administración de usuarios.

## Avance del 16 de noviembre

| Operación | Estado | Evidencia o siguiente paso |
| --- | --- | --- |
| Tabla de usuarios | Listo | V1 aplicada en RDS. Tabla e historial de Flyway comprobados. |
| Registro | Listo | Probado en EC2 con Postman. El correo duplicado devuelve 409. |
| Login con JWT | Listo | Probado con MySQL local: 200 con JWT y 401 con contraseña incorrecta. |
| Listar usuarios | En avance | Controlador, servicio y consulta inicial en Git. Falta habilitar el acceso con JWT y verificar permisos. |
| Modificar usuario | En avance | Controlador, validación y actualización inicial en Git. Falta proteger la ruta y probarla. |
| Desactivar usuario | En avance | La operación pone activo en false. Falta proteger la ruta y probarla. |
| Swagger UI | Implementado | OpenAPI respondió 200 en local. Falta abrir Swagger UI en EC2 después del despliegue. |
