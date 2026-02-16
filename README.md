# Foro Hub API

API REST construida con Java 17 + Spring Boot para gestión de tópicos de un foro.

## Tecnologías

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (Auth0 Java JWT)
- Swagger/OpenAPI (springdoc)
- Bean Validation
- Flyway Migration
- MySQL
- Maven

## Funcionalidades implementadas

- Registro de tópico (`POST /topicos`)
- Listado paginado de tópicos (`GET /topicos`)
- Filtros opcionales por `curso` y `anio` en el listado
- Detalle de tópico por ID (`GET /topicos/{id}`)
- Actualización de tópico (`PUT /topicos/{id}`)
- Eliminación de tópico (`DELETE /topicos/{id}`)
- Validación de campos obligatorios
- Regla de no duplicar tópico (`titulo + mensaje`)
- Migraciones automáticas de base de datos con Flyway
- Autenticación de usuarios con Spring Security
- Control de acceso con JWT Bearer en todos los endpoints protegidos

## Modelo de tópico

Campos persistidos:

- `id`
- `titulo`
- `mensaje`
- `fecha_creacion`
- `status`
- `autor`
- `curso`

## Reglas de negocio

- Todos los campos del registro/actualización son obligatorios.
- No se permiten tópicos duplicados con el mismo `titulo` y `mensaje`.
- Para operaciones por ID, el ID debe ser mayor que 0.
- Si un tópico no existe, la API responde `404 Not Found`.

## Endpoints

### Crear tópico

`POST /topicos`

Body JSON:

```json
{
  "titulo": "Duda sobre JPA",
  "mensaje": "Como mapear relaciones OneToMany?",
  "autor": "Andres",
  "curso": "Spring Boot"
}
```

Respuesta esperada: `201 Created`

### Listar tópicos

`GET /topicos`

Parámetros opcionales:

- `page` (default 0)
- `size` (default 10)
- `curso`
- `anio`

Ejemplos:

- `GET /topicos?page=0&size=10`
- `GET /topicos?curso=Spring%20Boot`
- `GET /topicos?anio=2026`
- `GET /topicos?curso=Spring%20Boot&anio=2026`

### Detalle de tópico

`GET /topicos/{id}`

Respuesta esperada: `200 OK` o `404 Not Found`

### Actualizar tópico

`PUT /topicos/{id}`

Body JSON (mismo formato que creación).

Respuesta esperada: `200 OK`, `404 Not Found` o `409 Conflict` (duplicado).

### Eliminar tópico

`DELETE /topicos/{id}`

Respuesta esperada: `204 No Content` o `404 Not Found`.

### Login

`POST /login`

Body JSON:

```json
{
  "login": "<tu_usuario>",
  "clave": "<tu_clave>"
}
```

Respuesta esperada: `200 OK` o `401 Unauthorized`.

Respuesta ejemplo:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Seguridad

- `POST /login` está público.
- El resto de endpoints requiere usuario autenticado.
- La API usa token JWT Bearer.
- Usuario inicial de prueba creado por migración Flyway:
  - `login`: `<usuario_configurado>`
  - `clave`: `<clave_configurada>`
- Tabla de autenticación: `usuarios` (migración `V3__create_table_usuarios.sql`)

Para consumir endpoints protegidos:

```http
Authorization: Bearer <token>
```

Comportamiento de acceso:

- token ausente o inválido: `401 Unauthorized`
- token válido: acceso permitido según endpoint

## Pruebas rápidas

Archivo JSON de login:

- `requests/login.json`

Archivo HTTP de ejemplo (VS Code REST Client / IntelliJ HTTP Client):

- `requests/foro-hub.http`

Con `curl`:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d @requests/login.json | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/topicos?page=0&size=10"
```

Para crear un tópico con token:

```bash
curl -X POST http://localhost:8080/topicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"titulo":"Topico JWT","mensaje":"Prueba","autor":"Andres","curso":"Spring Boot"}'
```

## Configuración local

Variables de entorno requeridas (información sensible fuera del código):

```bash
DB_URL=jdbc:mysql://127.0.0.1:3306/foro_hub
DB_USER=<tu_usuario_db>
DB_PASSWORD=<tu_password>
JWT_SECRET=<tu_secret_largo_y_aleatorio>
JWT_EXPIRATION=7200000
```

Puedes usar el archivo de ejemplo:

- `.env.example`

Recomendado:

1. Copiar `.env.example` a `.env`
2. Reemplazar valores sensibles en `.env`
3. Cargar variables antes de ejecutar la app

Ejemplo de carga en bash/zsh:

```bash
set -a
source .env
set +a
```

Crear BD local:

```sql
CREATE DATABASE foro_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Ejecución

```bash
./mvnw spring-boot:run
```

Archivo de configuración: `src/main/java/com/forohub/config/SecurityConfiguration.java`

Guía de ejecución persistente:

- `docs/COMO_EJECUTAR.md`

## Documentación API (Swagger)

Con la app en ejecución:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Para probar endpoints protegidos en Swagger:

1. Ejecuta `POST /login` y copia el `token`.
2. Click en `Authorize` en Swagger UI.
3. Pega: `Bearer <token>`.
4. Ejecuta los endpoints de tópicos desde la UI.
