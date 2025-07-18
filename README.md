# 🧪 Reactive Challenge API

Este proyecto consiste en el desarrollo de una API REST reactiva utilizando **Spring WebFlux** y **Java 21**, como parte de una capacitación técnica. La aplicación implementa una serie de funcionalidades orientadas a la integración de servicios externos, cacheo en Redis, persistencia en PostgreSQL y MongoDB, uso de Kafka para eventos, y testing completo del sistema.


## 🚀 Ejecución del proyecto

Seguí estos pasos para levantar la aplicación y realizar pruebas locales:

1. **Cloná el repositorio**
   ```bash
   git clone https://github.com/MicaelaLucero/reactive_training.git
   cd reactive_training
   ```

2. **Ejecución con imagen publicada en Docker Hub**

La API utiliza la imagen publicada en Docker Hub, por lo que **no es necesario construir la imagen localmente**. Solo ejecutá:
   ```bash
      docker-compose up
   ```

Esto descargará la imagen desde Docker Hub y levantará automáticamente todos los servicios necesarios:

- PostgreSQL
- Redis
- MongoDB
- Kafka + Zookeeper
- La API desarrollada

Para detener los contenedores:

   ```bash
      docker-compose down
   ```

> 💡 Asegurate de tener Docker en funcionamiento antes de ejecutar los comandos.


3. **Verificá que los servicios estén activos**  
   Podés hacer un GET a:
   ```
   http://localhost:8083/learning-reactive/external-api/integrations
   ```

4. **Probá los endpoints con Postman**  
   Descarga el archivo desde la raíz del proyecto e importa en Postman: reactive_challenge.postman_collection.json


5. **Ejecutá los tests desde la IDE o por línea de comandos**
    - Desde IntelliJ / VSCode: `Run All Tests`
    - Desde consola:
   ```bash
   gradle test
   ```
---

> ✅ Todos los servicios están expuestos en `localhost:8083`, y pueden probarse directamente una vez que el entorno esté levantado.


## ✅ Funcionalidades implementadas

### 1. Cálculo con porcentaje dinámico

- Se expone un endpoint que suma dos números y aplica un porcentaje extra obtenido de una fuente externa simulada.

### 2. Cacheo en Redis

- El porcentaje externo se guarda en Redis con un TTL de 30 minutos.
- En caso de falla del servicio, se utiliza el valor en caché si está disponible.
- Si no hay valor, se responde con un error claro al cliente.

### 3. Reintentos y eventos en Kafka

- Si el servicio externo falla, se reintenta hasta 3 veces.
- Tras fallos sucesivos, se emite un evento a Kafka con el detalle del error.
- Este evento se consume y se registra sin interrumpir el flujo principal.

### 4. Gestión de usuarios autorizados

- Se permite crear, obtener, listar y eliminar usuarios.
- La persistencia se realiza mediante PostgreSQL y R2DBC.
- Incluye validaciones de entrada y manejo de errores.

### 5. Historial de llamadas

- Se registra de forma asíncrona cada llamada a la API, con su respuesta o error.
- Los registros se almacenan en MongoDB.
- Solo los usuarios autorizados pueden consultar el historial.

### 6. Manejo de errores y validaciones

- Se implementa un manejador global para errores HTTP 4XX y 5XX.
- Se validan los inputs de forma estructurada para evitar errores inesperados.

### 7. Enfoque funcional

- Toda la API está construida con **routers y handlers**, respetando el enfoque funcional de Spring WebFlux.
- Se favorece el desacoplamiento entre rutas, lógica y persistencia.

### 8. Testing

- Se incluyeron tests **unitarios** y **de integración**, cubriendo:
    - Casos exitosos y fallidos del cálculo.
    - Reintentos y fallback a caché.
    - Acceso restringido y correcto al historial.

---

## ✅ Verificar servicios activos

**GET** `/learning-reactive/external-api/integrations`
- **Descripción:** Verifica si los servicios externos están activos.

---

## 📊 Obtener porcentaje

**GET** `/learning-reactive/external-api/percentage`
- **Descripción:** Devuelve un valor porcentual desde un servicio externo simulado.

---

## ➗ Realizar cálculo

**POST** `/learning-reactive/calculation`
- **Descripción:** Aplica el porcentaje a la suma de dos números enviados.
- **Body (JSON):**
```json
{
  "number_1": 2.5,
  "number_2": 5.0
}
```

---

## 👤 Crear usuario

**POST** `/learning-reactive/users`
- **Descripción:** Crea un nuevo usuario en el sistema.
- **Roles disponibles:** `"ADMIN"` o `"USER"`
- **Body (JSON):**
```json
{
  "name": "johndoe",
  "email": "johndoe3@gmail.com",
  "role": "ADMIN"
}
```

---

## ❌ Eliminar usuario

**DELETE** `/learning-reactive/users/{id}`
- **Descripción:** Elimina un usuario por su ID.

---

## 🔍 Obtener usuario por ID

**GET** `/learning-reactive/users/{id}`
- **Descripción:** Retorna el usuario asociado al ID.

---

## 📋 Obtener todos los usuarios

**GET** `/learning-reactive/users`
- **Descripción:** Lista todos los usuarios registrados.

---

## 🧹 Borrar caché de porcentaje

**DELETE** `/learning-reactive/cache/percentage`
- **Descripción:** Limpia el valor guardado en caché.

---

## 📥 Obtener porcentaje desde caché

**GET** `/learning-reactive/cache/percentage`
- **Descripción:** Devuelve el valor de porcentaje guardado en caché.

---

## 💾 Guardar porcentaje en caché

**POST** `/learning-reactive/cache/percentage`
- **Descripción:** Guarda un nuevo valor de porcentaje en caché.
- **Body (plain text):**
```
10
```

---

## 🕓 Obtener historial

**GET** `/learning-reactive/history`
- **Descripción:** Devuelve el historial de cálculos realizados.
- **Header:**
    - `X-USER-ID: 38ed1ffb-3777-486a-9d48-7f1e07aa2ff8`

---

## Autor

Desarrollado por **Micaela Lucero**.
Cualquier sugerencia o mejora es bienvenida. 🚀
