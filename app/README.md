# Heat Tourism - Setup Mapbox (equipo)

Este proyecto usa Mapbox en el Bloque B (mapa, localizacion y rutas).

## 1) Token secreto de descargas (`sk...`)

Este token **no** va en el repo. Debe vivir en tu maquina.

1. Crea/abre el archivo:
   - `C:\Users\<tu_usuario>\.gradle\gradle.properties`
2. Agrega esta linea con tu valor real:

```properties
MAPBOX_DOWNLOADS_TOKEN=sk.TU_TOKEN_REAL
```

Tambien puedes usar variable de entorno `MAPBOX_DOWNLOADS_TOKEN`.

## 2) Token publico de la app (`pk...`)

Este token se usa en tiempo de ejecucion en Android.

1. Abre `app/src/main/res/values/strings.xml`
2. Reemplaza el placeholder por tu token publico:

```xml
<string name="mapbox_access_token" translatable="false">pk.TU_TOKEN_PUBLICO</string>
```

## 3) Verificar compilacion

Desde la raiz del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
```

## 4) Regla importante para GitHub

- Nunca subir tokens reales (`pk...` o `sk...`).
- En commits compartidos deben quedar placeholders:
  - `PEGAR_AQUI_EL_PUBLIC_TOKEN`
  - `PEGAR_AQUI_EL_DOWNLOADS_TOKEN`

## 5) Si falla por secretos al hacer push

GitHub Push Protection puede bloquear el push si detecta tokens.
En ese caso, reemplaza los valores reales por placeholders, haz commit/amend y vuelve a pushear.
