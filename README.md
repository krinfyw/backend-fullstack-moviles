# Sonido Vivo · API REST

API REST de la tienda online de instrumentos y equipos musicales de Sonido Vivo (Viña del Mar, Región de Valparaíso), hecha con Spring Boot para DSY1104 Desarrollo Fullstack II (Duoc UC, 2026). La consume el [frontend en React](https://github.com/martin-perezr/audio-vivo-react).

La API corre en una instancia EC2 de AWS y guarda los datos en MySQL sobre Amazon RDS. JdbcTemplate ejecuta SQL con parámetros, Flyway versiona el esquema y springdoc genera la documentación OpenAPI y Swagger UI. El alcance de este avance es el registro, el inicio de sesión con JWT y el inicio de la administración de usuarios.

## Documentación de la API (Swagger)

| Qué | Dirección |
| --- | --- |
| Swagger UI | http://IP-ec2:8080/swagger-ui.html |
| OpenAPI (JSON) | http://IP-ec2:8080/v3/api-docs |

En local, las mismas rutas funcionan con `http://localhost:8080`.

La IP pública de la EC2 cambia cada vez que la instancia se detiene y se vuelve a iniciar. Si la dirección no responde, hay que revisar la IP actual en la consola de AWS (EC2 → Instances → Public IPv4 address) y actualizar esta tabla.

Para probar las rutas protegidas desde Swagger: hacer login en `POST /api/v1/auth/login`, copiar el `token` de la respuesta, presionar **Authorize** y pegarlo.

## Integrantes

- Santiago Aburto (san.matheo@duocuc.cl)
- Vicente Aninao (vi.aninao@duocuc.cl)
- Martín Pérez (mart.perezr@duocuc.cl)


## Tecnologías

| Uso | Herramienta |
| --- | --- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 4 (Web MVC, Validation) |
| Seguridad | Spring Security, OAuth2 Resource Server (JWT) y Argon2 (Bouncy Castle) |
| Acceso a datos | JdbcTemplate |
| Base de datos | MySQL en Amazon RDS |
| Migraciones | Flyway |
| Documentación | springdoc-openapi (Swagger UI) |
| Compilación | Maven (incluye `mvnw`) |
| Servidor | Amazon EC2 (Ubuntu) |

## Endpoints

Todas las rutas parten de `/api/v1`.

| Método | Ruta | Acceso | Qué hace |
| --- | --- | --- | --- |
| POST | `/auth/register` | Público | Crea una cuenta de cliente. Responde 201. |
| POST | `/auth/login` | Público | Devuelve el JWT y los datos del usuario. |
| GET | `/usuarios` | ADMIN | Lista los usuarios. |
| PUT | `/usuarios/{id}` | ADMIN | Modifica nombre y correo. Responde 204. |
| DELETE | `/usuarios/{id}` | ADMIN | Desactiva la cuenta. Responde 204. |

Cualquier otra ruta responde 403.

**Registro**

```json
{
  "nombre": "Ana Rojas",
  "correo": "ana@correo.cl",
  "password": "una-clave-de-12-o-mas"
}
```

La contraseña debe tener entre 12 y 128 caracteres.

**Login**

```json
{
  "correo": "ana@correo.cl",
  "password": "una-clave-de-12-o-mas",
  "aplicacion": "SONIDO_VIVO"
}
```

`aplicacion` es opcional: acepta `SONIDO_VIVO` (valor por defecto) o `MOVIL`. La respuesta trae `id`, `nombre`, `rol`, `token` y `expiraEnSegundos`.

Las rutas de ADMIN se llaman con el encabezado `Authorization: Bearer <token>`.

**Códigos de respuesta**

| Código | Cuándo |
| --- | --- |
| 400 | Faltan datos o no cumplen las validaciones. |
| 401 | Credenciales inválidas, cuenta desactivada o token ausente o vencido. |
| 403 | El token es válido pero el rol no es ADMIN. |
| 404 | El usuario no existe. |
| 409 | El correo ya está registrado. |

## Cómo configurar

Se necesitan Java 21 y una base MySQL (local o RDS). Maven no hace falta: el proyecto trae `mvnw`.

Las claves reales **no se suben a GitHub**. Se escriben en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://<endpoint-rds>:3306/sonido_vivo_DB
spring.datasource.username=<usuario>
spring.datasource.password=<contraseña>

# Secreto del JWT. Se genera con: openssl rand -hex 32
app.jwt.secret=<secreto-generado>

app.cors.origen=http://IP-ec2:5173
```

`app.cors.origen` acepta un solo origen. Si el frontend se abre desde otra dirección, el navegador bloquea las peticiones por CORS.

Flyway crea las tablas en el primer arranque con las migraciones de `src/main/resources/db/migration`.

## Cómo desplegar en EC2

1. Levantar la instancia EC2 desde la consola de AWS.
2. Conectarse a la instancia por SSH.
3. Modificar `src/main/resources/application.properties` con los datos del RDS (ver [Cómo configurar](#cómo-configurar)).
4. Generar el secreto del JWT, copiarlo y pegarlo en `app.jwt.secret`:

   ```bash
   openssl rand -hex 32
   ```

5. Iniciar Spring Boot. La API queda en el puerto 8080:

   ```bash
   ./mvnw spring-boot:run
   ```

6. Clonar el repositorio del frontend en la misma instancia:

   ```bash
   git clone https://github.com/martin-perezr/audio-vivo-react.git
   cd audio-vivo-react
   ```

7. Crear el archivo `.env.local` en la raíz del frontend con la IP pública de la EC2:

   ```
   VITE_API_URL=http://<IP-PUBLICA-EC2>:8080
   ```

8. Instalar las dependencias y levantar el frontend abierto hacia afuera:

   ```bash
   npm install
   npm run dev -- --host 0.0.0.0
   ```

9. Abrir `http://<IP-PUBLICA-EC2>:5173` en el navegador.

## Despliegue en AWS

| Recurso | Configuración |
| --- | --- |
| EC2 | Ubuntu con Java 21. La API corre en el puerto 8080 y el frontend en el 5173. |
| Security Group de la EC2 | Entradas TCP 22 (SSH), 8080 (API) y 5173 (frontend). |
| RDS | MySQL. Su Security Group permite el puerto 3306 desde el Security Group de la EC2. |

Cada vez que cambia la IP pública de la EC2 hay que actualizar `app.cors.origen` en el backend, `VITE_API_URL` en el `.env.local` del frontend y la dirección de Swagger en este README.

## Base de datos

El esquema está en `V1__crear_esquema_inicial.sql`.

| Tabla | Contenido |
| --- | --- |
| `usuario` | Cuentas compartidas por Sonido Vivo y la app móvil. |
| `usuario_aplicacion` | Rol de cada cuenta en cada aplicación. Sin fila, la cuenta no entra a esa aplicación. |
| `categoria` y `producto` | Catálogo de la tienda. |
| `pedido` y `detalle_pedido` | Pedidos y sus líneas. El total lo calcula el servidor. |
| `mensaje_contacto` | Mensajes del formulario Contáctanos. |

`V2__cuentas_movil_de_prueba.sql` agrega dos cuentas de prueba de la app móvil: `docente@duoc.cl` (DOCENTE) y `asistente@duoc.cl` (ASISTENTE).

El registro siempre crea clientes. Para tener un administrador de Sonido Vivo, se cambia el rol directamente en la base:

```sql
UPDATE usuario_aplicacion ua
JOIN usuario u ON u.id = ua.usuario_id
SET ua.rol = 'ADMIN'
WHERE u.correo = 'correo-del-admin@correo.cl' AND ua.aplicacion = 'SONIDO_VIVO';
```

## Estructura del proyecto

```
src/main/
├── java/com/example/backend_fullstack_moviles/
│   ├── auth/         Registro, login, JWT (JwtConfig) y seguridad y CORS (SeguridadConfig)
│   ├── usuarios/     Listar, modificar y desactivar usuarios
│   ├── OpenApiConfig.java                     esquema Bearer para Swagger
│   └── BackendFullstackMovilesApplication.java
└── resources/
    ├── application.properties                 configuración con valores de ejemplo
    └── db/migration/                          migraciones de Flyway (V1, V2)
```

Cada módulo separa el controlador (rutas y validación), el servicio (reglas) y el repositorio (SQL con JdbcTemplate).

## Repositorio del frontend

https://github.com/martin-perezr/audio-vivo-react