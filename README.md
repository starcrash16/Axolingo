<!--
  README for the Axolingo Android app
  Generated on December 1, 2025
-->

# Axolingo

Axolingo es una aplicación educativa móvil (Android) diseñada para enseñar Inglés y Matemáticas a niños mediante juegos interactivos, lecturas, quizzes y actividades prácticas.

---

**Resumen / Objetivo**

- **Objetivo:** Proveer una experiencia lúdica y didáctica para que niños aprendan vocabulario, lectura en inglés y conceptos matemáticos básicos a través de actividades guiadas y juegos.
- **Público objetivo:** Niños en edad escolar primaria, maestros y padres que buscan herramientas interactivas para el aprendizaje.

---

**Funcionalidades generales**

- Juegos interactivos: actividades tipo "Spelling Bee" y minijuegos (por ejemplo, Canasta Matemática).
- Lecciones de lectura con imágenes y quiz integrados por historia.
- Actividades de vocabulario con selección y traducción.
- Pizarra interactiva con reconocimiento de texto/dígitos (ML Kit) para actividades de escritura y reconocimiento.
- Registro e inicio de sesión (Firebase Authentication) y gestión de sesión local (DataStore).
- Almacenamiento y persistencia: Room Database para usuarios y puntuaciones.
- Seguimiento de progreso y puntuaciones por lección (se guardan y se muestran al usuario).
- Música de fondo controlable desde la app (play / pause / toggle).
- Interfaz accesible y botones personalizados para una experiencia enfocada en niños.

---

**Niveles & Estructura de aprendizaje**

La aplicación organiza el contenido en lecciones y niveles que se pueden ampliar:

- Nivel 1: Vocabulario básico y traducción (ejercicios de selección de palabras).
- Nivel 2: Lectura guiada (historias cortas con ilustraciones) + quiz de comprensión por historia.
- Nivel 3: Spelling / ortografía (juegos tipo Spelling Bee con arrastre/llenado).
- Nivel 4: Matemáticas básicas (minijuegos como Canasta Matemática que promueven cálculos simples).

Cada nivel puede contener varias lecciones y actividades; las puntuaciones permiten medir avance y mostrar feedback.

---

**Lecciones (ejemplos implementados)**

- Lección Inglés
  - ReadingActivity: lectura de historias por etapas con imágenes y Quiz (QuestionActivity).
  - VocabularyActivity / VocabActivity: ejercicios de vocabulario y práctica de palabras.
  - SpellingBeeActivity: minijuego para practicar la ortografía.

- Lección Matemáticas
  - ActividadCanasta: juego de atrapado de números y construcción de expresiones para resolver operaciones.
  - InterfazPizarra / PizarraMagica: pizarra para escribir números y reconocerlos.

- Otros controles reutilizables
  - ControlSelector / PizarraMagica: componentes para ejercicios de selección y drag-and-drop.

---

**Tecnologías y bibliotecas**

- Lenguaje: Kotlin
- Plataforma: Android (SDK)
- Persistencia: Room (AppDatabase)
- Autenticación: Firebase Authentication
- Reconocimiento de texto/dígitos: ML Kit (Text Recognition / Digital Ink - preparado)
- Serialización: Gson
- Concurrencia: Kotlin Coroutines / Flow
- UI: Views, ConstraintLayout, componentes personalizados

---

**Arquitectura y organización (carpetas principales)**

- `app/src/main/java/com/proyecto_final/axolingo/` — código principal de la app
  - `leccion_ingles/` — actividades de lectura, vocabulario y spelling
  - `leccion_mate/` — actividades y juegos de matemáticas
  - `pizarra/` — pizarra interactiva y lienzo de dibujo
  - `selector_palabras/` — componentes para selección/arrastre de palabras
  - `data/db/` — Room Database y DAOs
  - `session/` — DataStore para gestionar sesión local
  - `userSignForms/` — pantallas de SignIn/SignUp (Firebase)

---

**Creador(es) / Créditos**

- Proyecto original y mantenimiento: `starcrash16` (GitHub)
- Cocreador: `Harb3rt0` (GitHub)
- Contribuidores: (añadir nombres de colaboradores aquí)

Si quieres que aparezcan los nombres reales del equipo, reemplaza esta sección con la lista de autores y contactos.

---

**Puntos clave para describirlo en un README de GitHub (short bullets / pitch)**

- Aplicación Android educativa diseñada para niños: juegos, lecturas y ejercicios interactivos.
- Preferencia por UX simple y visual, con controles adaptados a público infantil.
- Integración con Firebase y ML Kit para autenticación y reconocimiento de escritura.
- Progreso y puntuaciones persistentes usando Room + DataStore.
- Código modular con componentes reutilizables (pizarra, selector de palabras, barra de progreso).

---

**Cómo ejecutar (desarrollo)**

1. Clona este repositorio:

```
git clone https://github.com/starcrash16/Axolingo.git
```

2. Abre el proyecto en Android Studio (Recomendado: Arctic Fox o superior).
3. Conecta un dispositivo o usa un emulador; para compilación rápida desde terminal en Windows:

```powershell
cd path\to\Axolingo
.\gradlew.bat assembleDebug
```

4. Si usas funciones de Firebase, asegúrate de añadir `google-services.json` en `app/`.

---

**Contribuir**

- Fork y PR: realiza cambios en una rama, abre Pull Request con descripción clara.
- Issues: reporta bugs o propuestas de features en GitHub Issues.

---

**Contacto**

- Repo: https://github.com/starcrash16/Axolingo

---


