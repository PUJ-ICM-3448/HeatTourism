# Entrega 2 — Implementación Mono-Usuario

Documentación de la Entrega 2 del proyecto **Heat Tourism**, asignatura Computación Móvil, Pontificia Universidad Javeriana.

**Fecha de entrega:** miércoles 29 de abril de 2026.

---

## 📋 Contenido de esta entrega

| Documento | Descripción |
|---|---|
| [evolucion-diseno.md](./evolucion-diseno.md) | Cambios entre Entrega 1 y Entrega 2 (clases, casos de uso, decisiones) |
| [arquitectura.md](./arquitectura.md) | Stack tecnológico, estructura de paquetes, decisiones técnicas |
| [funcionalidades.md](./funcionalidades.md) | Lista de funcionalidades por bloque con cómo demostrarlas |
| [diagrama-clases.puml](./diagrama-clases.puml) | Fuente PlantUML del diagrama de clases actualizado |
| [diagrama-casos-uso.puml](./diagrama-casos-uso.puml) | Fuente PlantUML del diagrama de casos de uso actualizado |

> Para renderizar los `.puml`: pegar el contenido en https://www.plantuml.com/plantuml/uml/ o usar el plugin de IntelliJ/Android Studio.

---

## ✅ Cumplimiento de la rúbrica

| Criterio | Peso | Estado | Dónde verificarlo |
|---|---:|:---:|---|
| Evolución de diagramas de diseño | 10% | ✅ | [evolucion-diseno.md](./evolucion-diseno.md) + diagramas |
| Acceso a hardware (cámara, galería, almacenamiento) | 20% | ✅ | Profile, CreatePost, ManageCompany |
| 2 sensores ≠ luminosidad | 15% | ✅ | Acelerómetro (Home) + Temperatura ambiente (TravelHome) |
| Mapas y localización | 20% | ✅ | Pantalla Map con Mapbox |
| Rutas entre 2 puntos | 20% | ✅ | RouteOverview con Directions API |
| Autenticación | 15% | ✅ | Firebase Auth (Register, LogIn, Profile) |
| **Bono — API mapas distinta a Google Maps** | **+0.5** | ✅ | **Mapbox** |

---

## 👥 Equipo Naranja Piña

- Valeria Herrera
- Tomás
- Miguel
- Jeison

División de bloques:

| Persona | Bloque |
|---|---|
| Bloque A — Autenticación Firebase | (asignar) |
| Bloque B — Mapas, localización y rutas | (asignar) |
| Bloque C — Cámara, galería y almacenamiento | (asignar) |
| Bloque D — Sensores, diagramas y documentación | Valeria |

---

## 🔗 Recursos

- **Repositorio:** https://github.com/PUJ-ICM-3448/HeatTourism
- **Rama integradora:** `develop`
- **PRs de la entrega:**
  - [#10](https://github.com/PUJ-ICM-3448/HeatTourism/pull/10) — Bloque A · Firebase Auth
  - [#11](https://github.com/PUJ-ICM-3448/HeatTourism/pull/11) — Bloque D · Sensores
  - [#12](https://github.com/PUJ-ICM-3448/HeatTourism/pull/12) — Bloque B · Mapas y rutas
  - [#13](https://github.com/PUJ-ICM-3448/HeatTourism/pull/13) — Bloque C · Hardware

---

## 📜 Entregas anteriores

- [Entrega 0](../entrega-0/) — Concepto inicial e ideación
- [Entrega 1](../entrega-1/) — Mockups, historias de usuario, primer diseño de clases
