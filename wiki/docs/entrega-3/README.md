# Entrega 3 — Implementación Multi-Usuario

Documentación de la Entrega 3 del proyecto **Heat Tourism**, asignatura Computación Móvil, Pontificia Universidad Javeriana.

---

## Contenido de esta entrega

| Documento | Descripción |
|---|---|
| [evolucion-diseno.md](./evolucion-diseno.md) | Cambios entre Entrega 2 y Entrega 3 (modelo, casos de uso, decisiones) |
| [arquitectura.md](./arquitectura.md) | Stack tecnológico, estructura, decisiones técnicas |
| [funcionalidades.md](./funcionalidades.md) | Lista de funcionalidades por bloque con cómo demostrarlas |
| [diagrama-clases.puml](./diagrama-clases.puml) | Fuente PlantUML del diagrama de clases actualizado |
| [diagrama-casos-uso.puml](./diagrama-casos-uso.puml) | Fuente PlantUML del diagrama de casos de uso |
| [diagrama-navegacion.puml](./diagrama-navegacion.puml) | Fuente PlantUML del grafo de navegación entre pantallas |

> Para renderizar los `.puml`: pegar el contenido en https://www.plantuml.com/plantuml/uml/ o usar el plugin de IntelliJ/Android Studio.

---

## Cumplimiento de la rúbrica

| Criterio | Peso | Estado | Dónde verificarlo |
|---|---:|:---:|---|
| Pitch + presentación | 25% | ✅ | Sustentación en clase |
| Manejo de BD dinámica + multi-usuario | 20% | ✅ | Chats real-time, amigos, FCM, Firestore |
| Seguimiento de posición en tiempo real | 20% | ✅ | `LocationTrackingService` + RouteMapScreen |
| Notificaciones | 15% | ✅ | FCM + NotificationsScreen + badge |
| Consumo de Servicios REST externos | 10% | ✅ | OpenWeather + RestCountries + QR generator |
| Acceso a hardware | 10% | ✅ | Cámara, galería, almacenamiento, 2 sensores |
| **Bono — Framework distinto** | +0.5 | ❌ | No implementado |
| **Bono — Wearables** | +0.5 | ❌ | No implementado |

---

## Equipo Naranja Piña

- Valeria Herrera
- Tomás
- Miguel
- Jeison

División de bloques en esta entrega:

| Persona | Bloque |
|---|---|
| Bloque A — Chats + Amigos + Notificaciones + FCM | Jeison |
| Bloque B — Tracking tiempo real + REST + Heat map | Tomás |
| Bloque C — Empresa + rutas + compras + ViewModels | Miguel |
| Bloque D — Setup + Navegación + Splash + Settings + diagramas + pitch | Valeria |

---

## Recursos

- **Repositorio:** https://github.com/PUJ-ICM-3448/HeatTourism
- **Rama integradora:** `develop`
- **PRs principales de esta entrega:**
  - Setup base (deps, permisos, enums) → `feature/entrega-3-setup`
  - Bloque A → `feature/persona1-chat-amigos`
  - Bloque B → `feature/bloque-b-tiempo-real-rest-mapa-calor`
  - Bloque C → `feat/e3-bloque-c`
  - Bloque D + correcciones → `feature/correcciones-finales`

---

## Entregas anteriores

- [Entrega 0](../entrega-0/) — Concepto inicial e ideación
- [Entrega 1](../entrega-1/) — Mockups, historias de usuario, primer diseño de clases
- [Entrega 2](../entrega-2/) — Implementación mono-usuario (auth, mapas, hardware, sensores)
