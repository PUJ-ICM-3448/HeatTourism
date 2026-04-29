# Bloque B – Setup local

Esta guía explica cómo activar el código del Bloque B (mapas + localización + rutas)
en el proyecto, **sin** subir nada al repo.

---

## 1. Crear la rama `feature/bloque-2` desde `develop`

> Estos comandos se corren en una terminal posicionada en la **raíz del repo**
> (`HeatTourism/app` no, es la raíz que contiene `README.md` y la carpeta `app/`).

```bash
# 1) Asegurar que git no marque cambios falsos por CRLF/LF
git config core.autocrlf true

# 2) Guardar mis cambios actuales (incluye el código del Bloque B que ya quedó escrito)
git add -A
git stash push -m "wip-bloque-b"

# 3) Bajar la última versión de develop y crear la rama
git fetch origin
git checkout -b feature/bloque-2 origin/develop

# 4) Re-aplicar mis cambios sobre la nueva rama
git stash pop
```

Si en el `stash pop` sale algún conflicto, abrir el archivo, dejar **mi versión**
(la del Bloque B) y resolver. Después:

```bash
git add -A
git commit -m "feat(B): mapas + localizacion + rutas con Mapbox"
```

> **No correr `git push`** hasta que tu equipo lo apruebe.

---

## 2. Configurar los tokens de Mapbox

Para que el SDK descargue y para que la API funcione hace falta crear **2 tokens**
en https://account.mapbox.com (cuenta gratis, no pide tarjeta).

### 2.1 Token secreto de descarga (`MAPBOX_DOWNLOADS_TOKEN`)

1. Mapbox → *Account* → *Tokens* → **Create a token**.
2. Marcar el scope `Downloads:Read` (es secreto).
3. Copiar el valor.
4. Pegarlo en `app/gradle.properties`, reemplazando `PEGAR_AQUI_EL_SECRET_TOKEN`.

> Mejor todavía: pegarlo en `~/.gradle/gradle.properties` para no commitearlo nunca.
> Ese archivo no se sube al repo y Gradle lo lee igual.

### 2.2 Token público de uso (`mapbox_access_token`)

1. En la misma página, crear otro token sin marcar scopes secretos
   (deja los públicos por defecto).
2. Copiar el valor.
3. Pegarlo en `app/app/src/main/res/values/strings.xml`,
   reemplazando `PEGAR_AQUI_EL_PUBLIC_TOKEN`.

---

## 3. Sincronizar Gradle

En Android Studio: *File → Sync Project with Gradle Files*. Si pide bajar el
SDK 36 o el plugin AGP nuevo, aceptar.

---

## 4. Probar

- Abrir la app, aceptar el permiso de ubicación cuando salga.
- Pestaña **Mapa**: debe aparecer el mapa de Barcelona con varios marcadores
  (Sagrada Familia, Park Güell, etc.). Tocar un marcador abre la tarjeta inferior.
- Tocar **Ver ruta hasta aquí**: navega a *RouteOverview*, dibuja la línea
  desde tu ubicación hasta el destino y muestra distancia y tiempo a pie.
- Pantalla **Ruta** → botón **Ver en mapa** ahora abre el RouteOverview hasta
  Sagrada Familia (placeholder mientras no haya detalle real de rutas).

Si no se aceptó el permiso, RouteOverview cae a *La Rambla* como punto de
partida para no romperse.
