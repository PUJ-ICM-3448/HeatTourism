# Funcionalidades implementadas — Entrega 3

Lista de funcionalidades por bloque con archivos y cómo demostrarlas.

---

## 🟦 Bloque A — Multi-usuario + Chats + Notificaciones

### A.1 Sistema de amigos
- **Pantalla:** `features/social/presentation/Friend/FriendScreen.kt`
- **Modelo:** `Amistad` con `estado: EstadoAmistad`
- **Repo:** `shared/social/FriendRepo.kt`
- **Cómo demostrar:**
  1. Persona 1: buscar a Persona 2 por username
  2. Enviar solicitud
  3. Persona 2 ve la solicitud, acepta
  4. Ambos ahora son amigos mutuos

### A.2 Chat real-time
- **Pantallas:** `ChatsListScreen.kt`, `ChatScreen.kt`
- **Modelos:** `Chat`, `Mensaje`
- **Repo:** `ChatRepo.kt` (con `addSnapshotListener` para tiempo real)
- **Cómo demostrar:**
  1. Persona 1 abre conversación con Persona 2
  2. Mandar mensaje
  3. Persona 2 (en otro dispositivo) ve el mensaje **sin refrescar**
  4. Persona 2 contesta, Persona 1 lo recibe en vivo

### A.3 Firebase Cloud Messaging (FCM)
- **Service:** `shared/notifications/HeatFirebaseMessagingService.kt`
- **Setup:** `shared/notifications/NotificationCenter.kt`
- **FCM tokens:** `NotificationRepo.syncFcmTokenForCurrentUser()`
- **Cómo demostrar:**
  1. Persona 1 está con la app cerrada o en background
  2. Persona 2 le manda mensaje en chat
  3. Persona 1 recibe **push notification** en la barra del sistema

### A.4 Notificaciones in-app
- **Pantalla:** `NotificationsScreen.kt`
- **Modelo:** `Notificacion` con tipo (`MENSAJE_CHAT`, `SOLICITUD_AMISTAD`, etc.)
- **Badge:** en bottom nav del perfil
- **Cómo demostrar:**
  1. Ir a Perfil → ver badge con número de no leídas
  2. Tap → NotificationsScreen muestra lista
  3. Marcar como leídas

---

## 🟩 Bloque B — Tracking tiempo real + REST + Heat map

### B.1 Location tracking en background
- **Service:** `data/service/LocationTrackingService.kt` (foreground service)
- **Provider:** `core/location/LocationProvider.kt`
- **Repo:** `core/location/LocationRepository.kt`
- **Cómo demostrar:**
  1. Iniciar viaje activo
  2. Verificar permisos (BACKGROUND_LOCATION concedido)
  3. En Firebase Console, ver que la colección `ubicaciones` se actualiza cada ~30s
  4. Cerrar app → ubicación se sigue actualizando

### B.2 Ver grupo en tiempo real en el mapa
- **Pantalla:** `features/map/presentation/RouteMapScreen.kt`
- **Cómo demostrar:**
  1. Persona 1 y Persona 2 en el mismo grupo de viaje
  2. Ambos entran a RouteMapScreen
  3. Cada uno ve el pin del otro
  4. Cambiar ubicación en uno (Extended Controls del emulador) → el otro ve el pin moverse

### B.3 REST #1 — OpenWeather API (clima del destino)
- **Client:** `core/network/OpenWeatherApi.kt`, `RetrofitClient.kt`
- **Repo:** `core/repo/WeatherRepository.kt`
- **Donde se usa:** `RouteOverviewScreen.kt`
- **Cómo demostrar:** abrir overview de un destino → ver temperatura real

### B.4 REST #2 — RestCountries API (info del país)
- **Client:** `data/network/CountryApi.kt`
- **Donde se usa:** `RouteOverviewScreen.kt`
- **Cómo demostrar:** abrir overview de destino → ver nombre del país + región + población

### B.5 QR Generator (invitar al grupo)
- **Util:** `data/qr/QRGenerator.kt` (ZXing)
- **Cómo demostrar:** en pantalla de grupo, opción "Invitar" → genera QR escanéable

### B.6 Heat map
- **Pantalla:** `MapScreen.kt`
- **Stack visual:** Google Maps SDK + Maps Utils (heatmap layer)
- **Cómo demostrar:** abrir Mapa → ver zonas con colores según densidad

---

## 🟨 Bloque C — Empresa + rutas + compras + comentarios

### C.1 Compras (Purchases)
- **Pantalla:** `features/route/presentation/Purchases/PurchasesScreen.kt` (con 3 tabs)
- **VM:** `PurchasesViewModel`
- **Modelo:** `GrupoViaje`
- **Cómo demostrar:** Profile → Mis compras → ver tabs "Ruta Actual", "Próximas", "Pasadas"

### C.2 BuyScreen
- **Pantalla:** `BuyScreen.kt`
- **VM:** `BuyViewModel`
- **Cómo demostrar:** RouteOverview → Comprar → escoger método pago → confirmar → crea Reserva en Firestore

### C.3 Comentarios y reacciones en posts
- **Pantalla:** `PostScreen.kt` + `PostViewModel`
- **Modelos:** `Comment`, `Reaccion`
- **Repos:** `SocialRepository`
- **Cómo demostrar:** abrir un post → comentar → reaccionar (corazón, fuego, etc.)

### C.4 CompanyScreen consolidado (3 modos)
- **Pantalla:** `features/company/presentation/Company/screen/CompanyScreen.kt`
- **VM:** `ViewCompanyViewModel`
- **Cómo demostrar:**
  - Como usuario normal: vista pública de empresa
  - Como coordinador: ve sus rutas asignadas
  - Como administrador: puede editar + acceder a CreateRoute

### C.5 CreateRouteScreen (admin)
- **Pantalla:** `CreateRouteScreen.kt` + `CreateRouteViewModel`
- **Cómo demostrar:** login con cuenta admin → CompanyScreen → Crear ruta → formulario → guardar

### C.6 HomeDispatcher
- **Composable:** `HomeScreen.kt` (`fun HomeDispatcher(navController)`)
- **Lógica:** decide entre `NoTravelHomeScreen` y `TravelHomeScreen` según viaje activo del usuario
- **Cómo demostrar:** sin viaje activo → home estándar. Comprar ruta → home con info del viaje.

---

## 🟧 Bloque D — Pantallas técnicas + Navigation + Settings

### D.1 SplashScreen
- **Pantalla:** `features/auth/presentation/splash/SplashScreen.kt`
- **Lógica:** observa `AuthViewModel`, decide ir a Home o LogIn
- **Salvavidas:** timeout de 4s si Firebase no responde
- **Cómo demostrar:** abrir app → logo + spinner → navega automáticamente

### D.2 RestorePwdScreen
- **Pantalla:** `features/auth/presentation/restore/RestorePwdScreen.kt`
- **Firebase:** `sendPasswordResetEmail`
- **Cómo demostrar:** LogIn → "¿Olvidaste tu contraseña?" → ingresar email → recibir reset email

### D.3 SettingsScreen
- **Pantalla:** `features/settings/presentation/SettingsScreen.kt`
- **Funciones:** switch notifs, info app, cerrar sesión (con limpieza de back stack)
- **Cómo demostrar:** Profile → ⚙️ → ver opciones → "Cerrar sesión" → vuelve a LogIn

### D.4 Sensor 1 — Acelerómetro (de Entrega 2, sigue funcionando)
- **Util:** `core/utils/ShakeDetector.kt`
- **Cómo demostrar:** en Home, sacudir el celular → Toast "Feed actualizado" + scroll + reshuffle

### D.5 Sensor 2 — Temperatura ambiente (de Entrega 2)
- **Util:** `core/utils/TemperatureSensor.kt`
- **Widget:** `core/component/TemperatureWidget.kt`
- **Cómo demostrar:** en TravelHome, ver widget de temperatura local vs destino

### D.6 Navigation reorganizado
- **Archivo:** `core/navigation/Navigation.kt`
- **Estructura:** enum `Screen` organizado por categorías (Auth, General, Chats, Rutas, Viaje, Empresa)
- **Rutas con parámetros:** Home, Post, RouteOverview, Buy, CheckIn, Chat, Company

---

## ✅ Hardware (mantener de Entrega 2)

### Cámara
- **API:** `ActivityResultContracts.TakePicture`
- **Permiso:** `CAMERA`
- **Donde:** Profile, CreatePost

### Galería
- **API:** `ActivityResultContracts.PickVisualMedia`
- **Permiso:** `READ_MEDIA_IMAGES`
- **Donde:** Profile, CreatePost

### Almacenamiento interno
- **API:** `Context.filesDir` + `FileProvider`
- **Config:** `res/xml/file_paths.xml`
- **Donde:** fotos de perfil y publicaciones

### Acelerómetro + Temperatura ambiente
- Ya descritos arriba (D.4 y D.5)

---

## 📊 Mapeo Rúbrica → Funcionalidades

| Rúbrica | Funcionalidades que la cumplen |
|---|---|
| **BD dinámica + multi-usuario (20%)** | A.1, A.2, A.3, A.4 (todo Bloque A) |
| **Seguimiento posición tiempo real (20%)** | B.1, B.2 |
| **Notificaciones (15%)** | A.3 (FCM), A.4 (in-app) |
| **Servicios REST (10%)** | B.3 (OpenWeather), B.4 (RestCountries), B.5 (QR) |
| **Hardware (10%)** | Cámara, galería, almacenamiento, D.4, D.5 |
| **Pitch (25%)** | Se mide en sustentación |
