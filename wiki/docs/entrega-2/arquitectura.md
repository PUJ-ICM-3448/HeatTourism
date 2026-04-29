# Arquitectura — Entrega 2

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Autenticación | Firebase Authentication |
| Base de datos | Cloud Firestore |
| Mapas | Mapbox Maps SDK for Android |
| Rutas | Mapbox Directions API |
| HTTP client | Ktor (CIO engine) |
| Serialización | kotlinx-serialization-json |
| Imágenes | Coil 3 |
| Sensores | `android.hardware.SensorManager` |
| Cámara | `ActivityResultContracts.TakePicture` + `FileProvider` |
| Galería | `ActivityResultContracts.PickVisualMedia` |
| min SDK / target | 24 / 36 |

## Estructura de paquetes

```
com.naranjapina.heat_tourism/
├── MainActivity.kt              # Entry point
├── component/                   # Componentes UI reutilizables
│   ├── DestinationCard.kt
│   ├── GroupPublication.kt
│   ├── TemperatureWidget.kt    # 🆕 Widget del sensor de temperatura
│   └── ...
├── data/
│   ├── MapboxDirectionsApi.kt   # Cliente HTTP a Mapbox
│   ├── SampleDestinations.kt    # Destinos hardcoded para mono-usuario
│   └── model/
│       ├── Usuario.kt
│       ├── Enums.kt
│       ├── MapPoint.kt
│       └── RouteSummary.kt
├── layout/
│   └── MenuBottonLayout.kt
├── navigation/
│   └── Navigation.kt            # NavHost con todas las pantallas
├── screen/                      # Pantallas
│   ├── Home.kt                  # NoTravelHome + TravelHome
│   ├── LogIn.kt, Register.kt
│   ├── Profile.kt
│   ├── Map.kt, Route.kt, RouteOverview.kt
│   ├── CreatePost.kt
│   └── ...
├── shared/auth/
│   └── AuthViewModel.kt         # Estado de autenticación
├── ui/theme/                    # Theme, Color, Type
└── utils/                       # 🆕 Utilidades (sensores, mapbox, etc.)
    ├── ShakeDetector.kt        # 🆕 Sensor acelerómetro
    ├── TemperatureSensor.kt    # 🆕 Sensor temperatura ambiente
    ├── MockData.kt             # 🆕 Pool de destinos/publicaciones
    ├── MapboxConfig.kt
    ├── LocationUtils.kt
    └── ...
```


## Permisos declarados

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

Los permisos "dangerous" (cámara, ubicación) se solicitan también en runtime.

## Decisiones de arquitectura

### 1. Composables tipo "hook" para sensores

Decidimos exponer los sensores como Composables (`rememberShakeDetector`, `rememberAmbientTemperature`) en lugar de ViewModels. Ventajas:

- Ciclo de vida automático: el listener se registra/desregistra con el Composable.
- API limpia para Compose: `val temp = rememberAmbientTemperature()`.
- No requiere DI ni configuración.

### 2. Datos mock para mono-usuario

En vez de poblar Firestore con datos de prueba, el Bloque D creó `MockData.kt` con pools de destinos y publicaciones hardcodeadas. Razón: la entrega es mono-usuario, no necesitamos persistencia compleja para datos que de todas formas no son del usuario actual.

### 3. AuthViewModel centralizado

El estado de autenticación se maneja en un único `AuthViewModel` que las pantallas observan. Permite reaccionar globalmente a logout (volver a Login) sin propagar callbacks.

### 4. FileProvider para imágenes

Las fotos tomadas con la cámara se guardan en el directorio privado de la app y se exponen vía `FileProvider` con la autoridad `${applicationId}.provider`. Configuración en `res/xml/file_paths.xml`.

### 5. Tokens fuera del repositorio

Las API keys de Mapbox y la configuración de Firebase no se commitean. Mapbox se configura vía `gradle.properties` local + `MapboxConfig.kt`. Firebase usa `google-services.json` que sí está en el repo (es público por diseño según Google).

