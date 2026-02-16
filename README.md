# Foro Hub API

API REST construida con Java 17 + Spring Boot para gestión de tópicos de un foro.

## Tecnologías

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security
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

## Configuración local

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/foro_hub
spring.datasource.username=andres
spring.datasource.password=mysql
```

Crear BD local:

```sql
CREATE DATABASE foro_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Ejecución

```bash
./mvnw spring-boot:run
```

## Nota de seguridad (temporal)

Actualmente la configuración de seguridad está abierta para facilitar el avance del curso:

- solicitudes permitidas sin autenticación
- CSRF desactivado

Archivo: `src/main/java/com/forohub/config/SecurityConfiguration.java`
