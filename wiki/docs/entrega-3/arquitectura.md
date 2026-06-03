# Arquitectura — Entrega 3

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Autenticación | Firebase Authentication |
| Base de datos en tiempo real | Cloud Firestore (snapshotListener) |
| Push notifications | Firebase Cloud Messaging (FCM) |
| Almacenamiento cloud | Firebase Storage |
| Mapas | Mapbox Maps SDK 11.16.0 |
| Mapas heatmap | Google Maps SDK + Maps Utils (solo para visualización del heat map) |
| Rutas | Mapbox Directions API |
| REST clients | Retrofit 2.9.0 + Ktor 3.4.0 |
| QR codes | ZXing (`core` + `journeyapps:zxing-android-embedded`) |
| Serialización | kotlinx-serialization-json, Gson |
| Imágenes | Coil 3 |
| Sensores | `android.hardware.SensorManager` |
| Cámara | `ActivityResultContracts.TakePicture` + `FileProvider` |
| Galería | `ActivityResultContracts.PickVisualMedia` |
| Location tracking | `play-services-location` + foreground service |
| min SDK / target | 24 / 36 |

## Estructura de paquetes (Clean Architecture por feature)

```
com.naranjapina.heat_tourism/
├── MainActivity.kt
│
├── core/                                  # Compartido entre features
│   ├── component/                         # UI reusables
│   ├── design/theme/                      # Theme, Color, Type
│   ├── layout/MenuBottomLayout.kt
│   ├── location/                          # 🆕 LocationModel, Provider, Repo, Service
│   ├── navigation/Navigation.kt
│   ├── network/                           # 🆕 OpenWeatherApi, RetrofitClient, WeatherResponse
│   ├── repo/                              # 🆕 WeatherRepository
│   └── utils/                             # MapboxConfig, LocationUtils, ShakeDetector,
│                                          #    TemperatureSensor, MockData, etc.
│
├── data/
│   ├── auth/
│   │   ├── model/                         # AuthUser, UserRole, Tourist, Coordinator, etc.
│   │   └── repository/AuthRepo.kt
│   ├── group/GroupRepository.kt           # 🆕 Grupos de viaje
│   ├── model/SharedEnums.kt               # Enums compartidos multi-usuario
│   ├── network/CountryApi.kt              # 🆕 REST RestCountries
│   ├── qr/QRGenerator.kt                  # 🆕 Generador de QR
│   ├── service/
│   │   ├── LocationTrackingService.kt     # 🆕 Foreground service de ubicacion
│   │   └── MapboxDirectionsApi.kt
│   └── SampleDestinations.kt
│
├── features/
│   ├── auth/
│   │   ├── domain/usecase/                # 🆕 LoginUseCase, RegisterUseCase
│   │   └── presentation/
│   │       ├── component/                 # AuthInputText, GoogleButton
│   │       ├── login/                     # LogInScreen, LoginState, LoginViewModel
│   │       ├── register/                  # RegisterScreen, RegisterState, RegisterViewModel
│   │       ├── restore/RestorePwdScreen.kt   # 🆕 Bloque D
│   │       └── splash/SplashScreen.kt        # 🆕 Bloque D
│   │
│   ├── company/
│   │   ├── data/                          # 🆕 Company, CompanyException, CompanyRepo
│   │   ├── domain/usecase/                # 🆕 LoadCompanyDataUseCase, GetCompanyIdByAdmin
│   │   └── presentation/
│   │       ├── Company/screen/            # 🆕 CompanyScreen + ViewCompanyScreen consolidado
│   │       └── CreateRoute/CreateRouteScreen.kt
│   │
│   ├── home/
│   │   ├── domain/model/User.kt
│   │   └── presentation/Home/
│   │       ├── HomeScreen.kt               # Con NoTravelHome, TravelHome, HomeDispatcher
│   │       └── HomeViewModel.kt            # 🆕
│   │
│   ├── map/presentation/
│   │   ├── Map/
│   │   │   ├── MapScreen.kt                # Mapa de calor general
│   │   │   ├── RouteOverviewScreen.kt      # Con REST clima + país
│   │   │   └── RouteOverviewViewModel.kt   # 🆕
│   │   ├── RouteMapScreen.kt               # 🆕 Mapa de viaje activo en vivo
│   │   └── model/                          # MapPoint, RouteSummary
│   │
│   ├── route/
│   │   ├── data/                           # 🆕 Route, RouteRepository
│   │   └── presentation/
│   │       ├── Buy/                        # BuyScreen + BuyViewModel
│   │       ├── CreateRoute/CreateRouteScreen.kt
│   │       ├── Purchases/                  # PurchasesScreen + ViewModel (con tabs)
│   │       └── Route/RouteScreen.kt
│   │
│   ├── settings/presentation/
│   │   └── SettingsScreen.kt               # 🆕 Bloque D
│   │
│   ├── social/
│   │   ├── data/                           # 🆕 Post, Comment, SocialRepository, DestinationRepository
│   │   └── presentation/
│   │       ├── Chats/                      # 🆕 ChatScreen, ChatsListScreen
│   │       ├── Company/CompanyScreen.kt
│   │       ├── CreatePost/CreatePostScreen.kt
│   │       ├── Friend/FriendScreen.kt      # 🆕
│   │       ├── Notifications/NotificationsScreen.kt  # 🆕
│   │       ├── Post/                       # PostScreen + PostViewModel
│   │       ├── Profile/ProfileScreen.kt
│   │       └── Searcher/                   # SearcherScreen + ViewModel
│   │
│   └── travel/
│       ├── data/model/                     # 🆕 GrupoViaje, MiembroGrupo
│       └── presentation/CheckIn/CheckInScreen.kt
│
└── shared/
    ├── auth/
    │   ├── AuthState.kt
    │   └── AuthViewModel.kt
    ├── notifications/                      # 🆕 Bloque A
    │   ├── HeatFirebaseMessagingService.kt
    │   └── NotificationCenter.kt
    └── social/                             # 🆕 Bloque A
        ├── ChatRepo.kt
        ├── FriendRepo.kt
        └── NotificationRepo.kt
```

## Permisos declarados

```xml
<!-- Internet -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Hardware (Entrega 2) -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />

<!-- Localización -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Entrega 3: tracking en background -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

<!-- Entrega 3: notifications -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
```

Servicios registrados en el Manifest:
- `LocationTrackingService` (Bloque B) — foreground service para tracking
- `HeatFirebaseMessagingService` (Bloque A) — recibe FCM push notifications

## Decisiones de arquitectura

### 1. Composables tipo "hook" para sensores
Heredado de Entrega 2. `rememberShakeDetector`, `rememberAmbientTemperature` se registran/desregistran automáticamente con el ciclo de vida del Composable vía `DisposableEffect`.

### 2. Firestore con snapshotListener para tiempo real
En vez de polling, los repositorios (`ChatRepo`, `FriendRepo`, `NotificationRepo`, `LocationRepository`) usan `addSnapshotListener` que emite cambios automáticamente. Eso permite mensajes en chat sin refresh, badge actualizándose solo, etc.

### 3. Foreground service para location tracking
La ley de Android requiere foreground service (notificación visible) para acceso continuo a ubicación cuando la app está en background. `LocationTrackingService` cumple esto y publica a Firestore cada ~30s mientras hay viaje activo.

### 4. ViewModels por feature (en vez de uno gigante)
Cada pantalla compleja tiene su propio ViewModel con su State sealed class. Permite hacer pruebas más fáciles y aísla los efectos de un cambio.

### 5. AuthUser con `roles: List<UserRole>`
Permite que un usuario sea Coordinador Y Administrador a la vez sin duplicar cuentas. Las pantallas hacen `if (user.roles.contains(UserRole.ADMINISTRATOR))` para mostrar variantes.

### 6. FCM tokens en el documento del usuario
`AuthUser.fcmTokens: List<String>` (es lista porque un usuario puede tener varios dispositivos). `NotificationRepo.syncFcmTokenForCurrentUser()` lo registra al hacer login.

### 7. Una sola pantalla para "Company" con 3 modos
En lugar de tener `CompanyScreen`, `CompanyScreenForCoordinator` y `CompanyScreenForAdmin` separadas, el componente único decide por el rol del usuario qué vista mostrar. Mismo principio para `CheckInScreen`, `RouteOverviewScreen`, etc.

### 8. Splash con timeout de seguridad
La pantalla Splash espera al `AuthViewModel`, pero si Firebase Auth no responde en 4 segundos (red lenta, GMS desactualizado), navega a LogIn igual. Evita que la app se cuelgue para siempre.

### 9. Tokens fuera del repo
- **Mapbox secret token (`sk.*`)**: en `gradle.properties` local, ignorado con `git update-index --assume-unchanged`. NO se commitea.
- **Mapbox public token (`pk.*`)**: en `strings.xml`. Puede commitearse (es público).
- **OpenWeather API key**: hardcoded en el código (idealmente debería ir en gradle.properties también).
- **Google Maps API key**: en el Manifest (cuestionable, debería ir en strings.xml).

## Estado del MVP

| Funcionalidad | Estado |
|---|:---:|
| Splash con timeout | ✅ |
| Auth: registro, login, logout, persistencia | ✅ |
| Auth: recuperar contraseña | ✅ |
| Settings + cerrar sesión | ✅ |
| Sistema de amigos | ✅ |
| Chat real-time | ✅ |
| FCM push notifications | ✅ |
| Notificaciones in-app | ✅ |
| Comentarios y reacciones en posts | ✅ |
| Mapa con localización actual | ✅ |
| Ruta entre 2 puntos | ✅ |
| Heat map de destinos | ✅ |
| Tracking de ubicación en background | ✅ |
| Ver miembros del grupo en mapa tiempo real | ✅ |
| API clima (OpenWeather) | ✅ |
| API país (RestCountries) | ✅ |
| QR para invitar al grupo | ✅ |
| Cámara, galería, almacenamiento | ✅ |
| Acelerómetro (shake to refresh) | ✅ |
| Temperatura ambiente | ✅ (con fallback) |
| Empresa: crear, editar, gestionar | ✅ |
| Coordinador: ver grupo, alertas | ✅ |
| Administrador: CRUD rutas | ✅ (mock) |
