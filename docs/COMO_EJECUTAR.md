# Como Ejecutar Foro Hub

## 1. Requisitos

- Java 17+
- MySQL corriendo en local
- Base de datos `foro_hub` creada

## 2. Configurar variables de entorno

El proyecto usa variables de entorno para datos sensibles.

Si no existe `.env`, crearlo desde el ejemplo:

```bash
cp .env.example .env
```

Editar `.env` con tus valores reales.

## 3. Cargar variables del archivo `.env`

En bash/zsh:

```bash
set -a
source .env
set +a
```

## 4. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La API queda disponible en:

- `http://localhost:8080`

## 5. Probar login JWT

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  --data-binary @requests/login.json
```

La respuesta retorna un JSON con `token`.

## 6. Consumir endpoint protegido

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  --data-binary @requests/login.json | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/topicos?page=0&size=10"
```

## 7. Detener la aplicación

En la terminal donde corre Spring Boot: `Ctrl + C`.
