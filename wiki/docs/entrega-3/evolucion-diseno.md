# Evolución del diseño — Entrega 3

Este documento explica los cambios entre Entrega 2 (mono-usuario) y Entrega 3 (multi-usuario), qué se implementó nuevo y qué se replanteó.

---

## 1. Contexto

La Entrega 3 pide pasar de **mono-usuario** a **multi-usuario completo**, agregando:

- Base de datos dinámica con sincronización en tiempo real (chats, amigos, notificaciones)
- Seguimiento de posición en tiempo real (location tracking en background)
- Notificaciones push (FCM) e in-app
- Consumo de servicios REST externos (clima, países, QR)
- Mantener el acceso a hardware ya implementado en Entrega 2

Esto implicó pasar de mocks hardcodeados a Firestore para todas las funcionalidades sociales y de coordinación de viajes.

---

## 2. Cambios en el diagrama de clases

### 2.1. Clases nuevas que entraron en producción

Estas clases estaban marcadas como `<<futuro>>` en Entrega 2 y ahora están implementadas:

| Clase | Bloque que la implementó | Persiste en |
|---|---|---|
| `Amistad` | A | Firestore `friendships` |
| `Chat`, `Mensaje` | A | Firestore `chats`, `messages` |
| `Notificacion` | A | Firestore `notifications` + FCM |
| `Post`, `Comment`, `Reaccion` | C | Firestore `posts` (y subcolecciones) |
| `GrupoViaje`, `MiembroGrupo` | C | Firestore `grupos` |
| `Company` | C | Firestore `companies` |
| `Route`, `PuntoRuta` | C | Firestore `routes` |
| `LocationUpdate` | B | Firestore `ubicaciones` (escrito por background service) |

### 2.2. Clases nuevas que aparecen por primera vez

| Clase | Para qué |
|---|---|
| `WeatherResponse` | Respuesta de OpenWeather API |
| `CountryResponse` | Respuesta de RestCountries API |
| `QRGenerator` | Utility para generar códigos QR de invitación a grupo |
| `LoginUseCase`, `RegisterUseCase` | Casos de uso de autenticación (clean architecture) |
| `*ViewModel` | Un ViewModel por feature (HomeViewModel, BuyViewModel, PurchasesViewModel, etc.) |

### 2.3. Cambio en `Usuario`/`AuthUser`

En Entrega 2 propusimos un `Usuario` con campo `tipo` (enum único). En Entrega 3 el equipo evolucionó a una clase `AuthUser` con campo `roles: List<UserRole>`. **Es una mejora** porque permite que un mismo usuario sea coordinador Y administrador a la vez sin tener que duplicar cuentas. También se agregó `fcmTokens: List<String>` para las push notifications.

### 2.4. Estructura por features (refactor del equipo)

El equipo refactorizó toda la organización del código pasando de una estructura por tipo (`screen/`, `component/`, `utils/`) a una **por feature** (`features/{nombre}/presentation/`, `features/{nombre}/domain/`, `features/{nombre}/data/`). Esto sigue Clean Architecture y deja el código mucho más escalable.

---

## 3. Cambios en los casos de uso

### 3.1. Actores ahora activos

| Actor | Entrega 2 | Entrega 3 |
|---|---|---|
| **Turista** | ✅ Único actor activo | ✅ Sigue siendo el principal |
| **Coordinador** | Diseñado, sin implementación | ✅ Puede iniciar viaje, emitir alertas, gestionar checkins |
| **Administrador (Empresa)** | Diseñado, sin implementación | ✅ Puede crear/editar rutas, gestionar empresa |

### 3.2. Casos de uso nuevos (técnicos que pide la rúbrica)

- **Chats en tiempo real** (Firestore snapshotListener) — Bloque A
- **Sistema de amigos** (solicitudes, aceptar, eliminar) — Bloque A
- **Recibir push notifications** (FCM background) — Bloque A
- **Ver notificaciones in-app** (NotificationsScreen) — Bloque A
- **Compartir ubicación automáticamente** (LocationTrackingService) — Bloque B
- **Ver miembros del grupo en mapa en vivo** — Bloque B
- **Ver clima del destino** (OpenWeather REST) — Bloque B
- **Ver info del país** (RestCountries REST) — Bloque B
- **Generar QR de invitación al grupo** — Bloque B
- **Comentar y reaccionar publicaciones** — Bloque C
- **Comprar ruta** (Reserva + Pago mock) — Bloque C
- **Coordinador: emitir alerta grupal** — Bloque C
- **Coordinador: aprobar/rechazar checkins** — Bloque C
- **Administrador: CRUD rutas** — Bloque C (mock)

### 3.3. Casos de uso re-categorizados

| Caso de uso | Entrega 2 | Entrega 3 |
|---|---|---|
| Ver mapa de calor | Solo pins mockeados | Heat map real con densidad de usuarios |
| Iniciar sesión coordinador | Pantalla separada | **Misma pantalla LogIn** (rol detectado por `roles: List<UserRole>` del Usuario) |

---

## 4. Decisiones de stack tecnológico nuevas

| Decisión | Alternativas consideradas | Por qué la elegimos |
|---|---|---|
| **Cloud Firestore con snapshotListener** | Polling con REST, Realtime Database | Sincronización automática, queries más expresivas, escala mejor |
| **Firebase Cloud Messaging (FCM)** | OneSignal, AWS SNS | Gratis, integración directa con el resto de Firebase |
| **Retrofit + OkHttp** | Ktor, plain HttpURLConnection | Estándar de la industria Android, plugin de logging fácil |
| **OpenWeatherMap API** | WeatherAPI, AccuWeather | Free tier generoso, datos confiables |
| **RestCountries API** | World Bank API | Sin auth, simple, perfecta para info país |
| **ZXing (QR generator)** | Google Vision API, otros | Local, sin red, sin permisos extra |
| **Google Maps SDK + heatmap utils** | Mapbox heatmap | El plugin de heatmap de Google Maps es muy maduro |
| **ViewModels por feature** | Un AuthViewModel monolítico | Mejor separación de responsabilidades |

---

## 5. Resumen de evolución por bloque

### Bloque A — Multi-usuario (chats, amigos, notif, FCM)
- Modelos `Amistad`, `Chat`, `Mensaje`, `Notificacion`
- Repositorios `FriendRepo`, `ChatRepo`, `NotificationRepo`
- 4 pantallas nuevas: `FriendScreen`, `ChatsListScreen`, `ChatScreen`, `NotificationsScreen`
- Firebase Cloud Messaging integrado (`HeatFirebaseMessagingService`, `NotificationCenter`)
- Badge de notificaciones no leídas en bottom nav del perfil

### Bloque B — Tracking + REST + Heat map
- `LocationTrackingService` (foreground service) que publica ubicación a Firestore cada ~30s
- `LocationRepository`, `LocationProvider`, `LocationModel` para abstraer el tracking
- `RouteMapScreen` con mapa en vivo del grupo + heat map de zonas calientes
- REST APIs: `OpenWeatherApi`, `CountryApi`, ambas con Retrofit
- `WeatherRepository`, `CountryRepository`
- `QRGenerator` para invitar a grupos
- Integración de Google Maps SDK + heatmap utils (para visualización)

### Bloque C — Empresa + rutas + compras + ViewModels
- Modelos `Company`, `Route`, `Post`, `Comment`, `GrupoViaje`, `MiembroGrupo`
- Repositorios `CompanyRepo`, `RouteRepository`, `SocialRepository`, `DestinationRepository`, `GroupRepository`
- 8 nuevos ViewModels (HomeViewModel, BuyViewModel, PurchasesViewModel, RouteOverviewViewModel, CreateRouteViewModel, PostViewModel, SearcherViewModel, ViewCompanyViewModel)
- `HomeDispatcher` que decide entre `NoTravelHome` y `TravelHome` según viaje activo
- Pantallas: `CreateRouteScreen` (admin), `PurchasesScreen` con tabs (actual/futuras/pasadas), `CompanyScreen` consolidado con 3 modos (general/coord/admin)
- Comentarios y reacciones en posts

### Bloque D — Navegación + Splash + Settings + diagramas + pitch
- Reorganización completa de `Navigation.kt` con categorías y rutas con parámetros
- 3 pantallas nuevas: `SplashScreen` (con timeout de seguridad), `RestorePwdScreen` (Firebase reset email), `SettingsScreen`
- Setup inicial (FCM + Storage deps, permisos background location, enums multi-usuario)
- Eliminación de `Route.kt` (consolidado en RouteOverview) y `RouteGroup.kt` (vacía)
- Diagramas evolucionados (clases, casos de uso, navegación)
- Wiki entrega-3 completa
- Pitch + presentación + guion de demo
- **Coordinación de los 4 merges PR → develop** (resolución manual de conflictos del refactor)

---

## 6. Lo que quedó sin implementar (consciente)

| Funcionalidad | Por qué |
|---|---|
| Wearables (Wear OS) | Requería dispositivo/emulador adicional, no había tiempo |
| Framework distinto (Flutter, React Native) | Esfuerzo alto vs +0.5 de bono |
| Sistema de pagos real | Pasarela de pago requiere SDK adicional y configuración; se simula con UI |
| Heat map con datos agregados reales | Necesitaría volumen de usuarios para tener datos. Por ahora muestra zonas hardcoded + densidad mockeada |
