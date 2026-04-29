# Funcionalidades implementadas — Entrega 2

Lista detallada de las funcionalidades implementadas, organizadas por bloque, con archivos involucrados y cómo demostrarlas.

---

## 🟦 Bloque A — Autenticación con Firebase

### A.1 Registro de usuario
- **Pantalla:** `Register.kt`
- **Cómo demostrar:** llenar formulario con email, contraseña y nombre → presionar "Registrarse" → ver redirección a Home.
- **Validaciones:** correo con formato válido, contraseña mínima de 6 caracteres, manejo de errores ("correo en uso").

### A.2 Inicio de sesión
- **Pantalla:** `LogIn.kt`
- **Cómo demostrar:** ingresar email y contraseña existente → ver Home.

### A.3 Persistencia de sesión
- **Componente:** `AuthViewModel.kt`
- **Cómo demostrar:** cerrar la app completamente, reabrirla → entra directo a Home sin pasar por Login.

### A.4 Cierre de sesión
- **Pantalla:** `Profile.kt`
- **Cómo demostrar:** desde el perfil presionar "Cerrar sesión" → vuelve a Register/Login.

### A.5 Manejo de errores
- Mensaje visible cuando el correo ya está en uso (registro).
- Mensaje cuando las credenciales son incorrectas (login).

---

## 🟩 Bloque B — Mapas, localización y rutas

### B.1 Mapa con ubicación actual
- **Pantalla:** `Map.kt`
- **Permisos:** `ACCESS_FINE_LOCATION` solicitado en runtime.
- **Componente:** `utils/LocationUtils.kt` y Mapbox Maps SDK.
- **Cómo demostrar:** abrir Map → conceder permiso → el mapa centra en tu ubicación.

### B.2 Marcadores de destinos
- **Datos:** `data/SampleDestinations.kt`.
- **Cómo demostrar:** ver pins de destinos sobre el mapa, hacer tap y ver info.

### B.3 Ruta entre dos puntos
- **API:** Mapbox Directions API.
- **Cliente:** `data/MapboxDirectionsApi.kt` (usa Ktor).
- **Pantalla:** `RouteOverview.kt`.
- **Cómo demostrar:** seleccionar destino → calcular ruta → ver línea sobre el mapa con distancia y tiempo.

### B.4 Bono — Mapbox en lugar de Google Maps
- Configuración: `utils/MapboxConfig.kt`, dependencias en `build.gradle.kts`.
- Token configurado vía `strings.xml` (`mapbox_access_token`).

---

## 🟨 Bloque C — Acceso a hardware

### C.1 Tomar foto con la cámara
- **API:** `ActivityResultContracts.TakePicture`.
- **Permiso:** `CAMERA` (runtime).
- **Pantallas:** `Profile.kt`, `CreatePost.kt`, `ManageCompany.kt`.

### C.2 Seleccionar imagen de la galería
- **API:** `ActivityResultContracts.PickVisualMedia`.
- **Permiso:** `READ_MEDIA_IMAGES` (Android 13+) / `READ_EXTERNAL_STORAGE` (Android ≤12).

### C.3 Almacenamiento interno
- **Mecanismo:** las fotos se guardan en el directorio privado de la app (`Context.filesDir`).
- **Compartición:** vía `FileProvider` configurado en `res/xml/file_paths.xml`.
- **Cómo demostrar:** tomar foto → cerrar y reabrir la app → la foto sigue ahí.

---

## 🟧 Bloque D — Sensores

### D.1 Acelerómetro — Shake to refresh
- **Sensor:** `Sensor.TYPE_ACCELEROMETER`.
- **Componente:** `utils/ShakeDetector.kt`.
- **Pantallas:** `NoTravelHomeScreen` y `TravelHomeScreen` (en `Home.kt`).
- **Comportamiento:**
  - Calcula magnitud de aceleración: `sqrt(x² + y² + z²) / GRAVITY_EARTH`.
  - Threshold: 2.3G (configurable en `SHAKE_THRESHOLD_GRAVITY`).
  - Debounce: 1 segundo entre shakes.
  - Al detectar shake:
    - Reordena aleatoriamente la lista de destinos/publicaciones (de un pool en `MockData.kt`).
    - Anima scroll suave hasta el inicio de la lista.
    - Muestra Toast confirmando "Feed actualizado".
- **Animaciones:**
  - Destinos: `Modifier.animateItem()` con fade-in 500ms / fade-out 300ms / placement 500ms.
  - Publicaciones: `Crossfade` con duración 500ms.
- **Cómo demostrar:** estar en Home, scrollear abajo, agitar el celular → ver fade + scroll arriba con cards distintas.

### D.2 Temperatura ambiente
- **Sensor:** `Sensor.TYPE_AMBIENT_TEMPERATURE`.
- **Componente:** `utils/TemperatureSensor.kt` y `component/TemperatureWidget.kt`.
- **Pantalla:** `TravelHomeScreen` (debajo del header del viaje activo).
- **Comportamiento:**
  - Lee temperatura local del sensor en grados Celsius.
  - Compara con temperatura del destino (mock por ahora).
  - Muestra hint contextual:
    - Diferencia > 5°C más cálido en destino → "¡Hará más calor allá! Lleva ropa ligera 🥵"
    - Diferencia > 5°C más frío → "Va a estar más fresco. Lleva un abrigo 🧥"
    - Parecidas → "Temperaturas parecidas, viaja cómodo 😎"
  - Si el dispositivo NO tiene sensor de temperatura ambiente (la mayoría de Pixel y emuladores no lo tienen):
    - Muestra "—" en la lectura local.
    - Muestra mensaje "Tu sensor de temperatura no está disponible".
    - Sigue mostrando la temperatura mock del destino.

### D.3 Por qué estos dos sensores
- **Acelerómetro:** está en todos los celulares, fácil de demostrar visualmente, encaja con la UX de "actualizar feed".
- **Temperatura ambiente:** encaja temáticamente con "Heat Tourism" — la app es sobre turismo y calor. Si el destino es más caliente que tu zona, te avisa.
- **Ninguno es luminosidad** ✅ (cumple requisito de la rúbrica).
