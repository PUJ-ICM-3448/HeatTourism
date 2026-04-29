# Evolución del diseño — Entrega 2

Este documento explica los cambios que sufrió el diseño de la aplicación entre la Entrega 1 y la Entrega 2, qué se implementó, qué se replanteó y qué quedó diseñado pero sin implementar (para Entrega 3).

---

## 1. Contexto

La Entrega 2 pide una **implementación mono-usuario** que demuestre acceso a hardware, sensores, mapas, rutas y autenticación. Esto nos obligó a hacer dos cosas:

1. Reducir el alcance del modelo conceptual original (que era multi-usuario) al subset que tiene sentido en mono-usuario.
2. Corregir errores conceptuales que detectamos al pasar de "diseño en papel" a código real.

---

## 2. Cambios en el diagrama de clases

### 2.1. Usuario sin `passwordHash`

**Antes:**
```
Usuario {
  passwordHash: String
  ...
}
```

**Ahora:**
```
Usuario {
  id, nombreCompleto, correo, fotoPerfilUrl, ...
  // sin passwordHash
}
```

**Razón:** la autenticación se delega a **Firebase Auth**, que maneja credenciales por separado de los datos de perfil. Guardar el password (o su hash) en nuestra propia colección sería:

- **Inseguro**: duplicar credenciales aumenta superficie de ataque.
- **Innecesario**: Firebase ya provee `FirebaseUser` con `uid`, `email` y verificación.
- **Mala práctica**: ningún sistema serio guarda passwords en su propia tabla cuando hay un proveedor de identidad.

### 2.2. Sin herencia en Usuario (Turista / Coordinador / Admin)

**Antes:**
```
Usuario (abstracta)
  ├── Turista
  ├── Coordinador
  └── AdministradorEmpresa
```

**Ahora:**
```
Usuario {
  tipo: TipoUsuario  // enum
  // campos específicos opcionales:
  nacionalidad: String?
  codigoEmpleado: String?
  cargo: String?
}
```

**Razón:** Firestore (la base de datos NoSQL de Firebase) **no maneja polimorfismo nativamente**. Serializar/deserializar jerarquías de clases requiere mucho código extra y propenso a errores. La solución estándar en Kotlin + Firestore es usar un campo `tipo` con un enum y dejar opcionales los campos específicos. Más simple, igual de expresivo, sin perder semántica.

### 2.3. `PerfilUsuario` consolidado en `Usuario`

**Antes:** `Usuario` tenía datos de identidad y `PerfilUsuario` (clase separada) tenía bio, ciudad, totales.

**Ahora:** todo en `Usuario`.

**Razón:** en mono-usuario no hay razón para separar dos documentos en Firestore por un usuario. Se hace un solo `read` y se obtiene todo. Reduce complejidad y costo de queries.

### 2.4. `PuntoRuta` referencia a `Lugar`

**Antes:** `PuntoRuta` y `Lugar` tenían campos duplicados (nombre, descripción, posición).

**Ahora:** `PuntoRuta.lugarId: String` apunta a `Lugar`. La info del lugar se mantiene en un solo sitio.

**Razón:** evitar duplicación. Si un lugar cambia su nombre o foto, no hay que actualizarlo en cada `PuntoRuta` que lo use.

### 2.5. Notificación con destinatario explícito

**Antes:** `Notificacion` no tenía a quién pertenece.

**Ahora:** `Notificacion.destinatarioId: String` (uid del Usuario).

**Razón:** sin esto no se podía consultar las notificaciones de un usuario específico.

### 2.6. Clases marcadas `<<futuro>>`

Marcamos como `<<futuro>>` aquellas clases que **forman parte del diseño completo** pero no se implementan en Entrega 2 porque requieren multi-usuario o backend agregado:

| Clase | Por qué se posterga |
|---|---|
| `MapaCalor`, `ZonaCalor`, `MetricaZona` | Heat map necesita datos agregados de muchos usuarios |
| `Comentario`, `Reaccion` | Requieren otros usuarios para tener sentido |
| `GrupoViaje`, `MiembroGrupo` | Multi-usuario por definición |
| `AlertaGrupo`, `LlamadoLista` | Coordinador + grupo → multi-usuario |
| `ListaAlcance` | Sistema de amigos → multi-usuario |

---

## 3. Cambios en los casos de uso

### 3.1. Reducción al actor Turista

En Entrega 1 había 3 actores activos: Turista, Coordinador, Empresa. En Entrega 2 **solo el Turista** está implementado. Coordinador y Empresa quedan en el diagrama como casos de uso `<<futuro>>` para mantener la trazabilidad del diseño.

### 3.2. Casos de uso nuevos (técnicos)

Aparecen casos de uso que no existían en Entrega 1 porque eran "internos del sistema":

- **Refrescar feed agitando el celular** (sensor acelerómetro).
- **Ver temperatura local vs destino** (sensor temperatura ambiente).
- **Tomar foto con la cámara** y **Seleccionar foto de la galería** (acceso a hardware).
- **Guardar imagen en almacenamiento interno** (storage).
- **Mantener sesión activa** (Firebase Auth).
- **Calcular ruta entre dos puntos** (Mapbox Directions API).

Estos casos de uso técnicos son los que pide la rúbrica de Entrega 2.

### 3.3. Casos de uso re-categorizados

| Caso de uso | Entrega 1 | Entrega 2 |
|---|---|---|
| Comprar ruta | Empresa | **Turista** (corrección: la compra siempre la hace el turista) |
| Mapa de calor en viaje | Pantalla aparte | Modo de la misma pantalla `Mapa` |

---

## 4. Decisiones de stack tecnológico

| Decisión | Alternativas consideradas | Por qué la elegimos |
|---|---|---|
| **Mapbox** | Google Maps, OpenStreetMap | Cumple bono +0.5, mejor estilo visual, integración limpia con Compose, Directions API incluida |
| **Firebase Auth** | Backend propio | Setup en 30 min vs varios días, autenticación robusta, manejo de sesión gratuito |
| **Firestore** | Room (SQLite local), backend propio | Sincronización en la nube sin servidor, escala a multi-usuario en Entrega 3 sin reescribir |
| **Jetpack Compose** | Vistas XML | Estándar moderno de Android, declarativo, mejor para sensores reactivos |
| **Coil 3** | Glide, Picasso | Más liviano, soporte nativo de Compose |
| **Ktor** | Retrofit | Más ligero, multiplataforma, suficiente para llamados a Mapbox Directions |

---

## 5. Resumen de evolución por bloque

### Bloque A — Autenticación
- Implementado: registro, login, logout, persistencia de sesión, manejo de errores.
- `AuthViewModel` para coordinar el estado de autenticación.

### Bloque B — Mapas y rutas
- Mapbox SDK integrado (no Google Maps).
- Ubicación actual del usuario en tiempo real.
- Marcadores de destinos.
- Cálculo y dibujo de ruta entre dos puntos con Mapbox Directions API.
- Permisos `ACCESS_FINE_LOCATION` solicitados en runtime.

### Bloque C — Hardware
- Cámara: tomar foto desde la app (`ActivityResultContracts.TakePicture`).
- Galería: seleccionar imagen (`PickVisualMedia`).
- Almacenamiento interno: guardar fotos en directorio privado de la app vía `FileProvider`.
- Pantallas afectadas: Profile, CreatePost, ManageCompany.

### Bloque D — Sensores
- **Acelerómetro** (`TYPE_ACCELEROMETER`): detección de shake con threshold 2.3G y debounce de 1s. Refresca el feed con animación de fade y scroll a top.
- **Temperatura ambiente** (`TYPE_AMBIENT_TEMPERATURE`): widget que compara temperatura local vs destino y sugiere qué ropa llevar. Fallback elegante si el sensor no está disponible.
- Diagramas y documentación actualizados.


