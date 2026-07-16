<div align="center">

# سَبِيل — Sabeel
*A premium digital Tasbih designed for the modern Muslim.*

[![Platform](https://img.shields.io/badge/Platform-Android-green?logo=android)](#)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue)](#)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)](#)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen?logo=jetpackcompose)](#)
[![Architecture](https://img.shields.io/badge/Architecture-MVI%20%2B%20Clean-orange)](#)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

## Project Name and Description

**Sabeel** (Arabic: سَبِيل — *the path, the way*) is a premium, distraction-free digital tasbih designed to help you build a consistent dhikr habit.

Sabeel is built on the philosophy of the **"Invisible Interface"**. Designed so you don't actually have to look at your screen. Anchored by a large, morphing glass-like circle on an OLED-black canvas, the app speaks to you entirely through touch, subtle ticks for counting, distinct clicks for milestones, and deep thuds for completions.

Whether you're commuting, walking, or focusing deeply after salah, Sabeel gets out of your way. It combines authentic Sahih-sourced adhkar and automatic post-salah sequencing (Smart Flow) with an incredibly polished, eyes-free experience.

## Technology Stack

- **Core Language:** Kotlin 2.4.x (JVM 17)
- **UI Framework:** Jetpack Compose + Material 3, utilizing `androidx.graphics.shapes`
- **Dependency Injection:** Dagger Hilt 2.x
- **Local Persistence:** Room v2, DataStore Preferences 1.x
- **Navigation:** Navigation Compose 2.x
- **Build System:** Gradle 9 (KTS) with KSP
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** API 36

## Project Architecture

Sabeel is built on **Clean Architecture** with a strict **MVI (Model-View-Intent)** pattern. Every layer has a single responsibility and communicates only through defined contracts.

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  HomeScreen  │  TasbihScreen  │  DhikrLibrary  │ Settings│
│              ↑ collectAsState()                         │
├─────────────────────────────────────────────────────────┤
│                   ViewModel Layer                       │
│  HomeViewModel │ TasbihViewModel │ DhikrViewModel │ ... │
│                ↑ Hilt @HiltViewModel                    │
├─────────────────────────────────────────────────────────┤
│                   Domain Layer                          │
│  Repository interfaces │ DhikrCatalog │ Domain models   │
├─────────────────────────────────────────────────────────┤
│                    Data Layer                           │
│   Room Database   │   DataStore Preferences             │
│  (sessions, streak,│  (live counter, active dhikr,      │
│   custom dhikr)   │   settings)                         │
└─────────────────────────────────────────────────────────┘
```

## Getting Started

### Prerequisites
- **Android Studio** Meerkat (or later)
- **JDK 17**
- Android device or emulator running **Android 7.0+ (API 24)**

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kutubuddin-rasel/sabeel.git
   cd sabeel
   ```

2. **Font Setup:**
   The app requires the **KFGQPC Uthmanic Script Hafs** font for accurate Arabic rendering. Ensure `KFGQPC Uthmanic Script HAFS Regular.otf` is present in the project root directory.

3. **Build and Install:**
   ```bash
   # Build debug APK
   ./gradlew :app:assembleDebug

   # Install on connected device
   ./gradlew :app:installDebug
   ```

## Project Structure

The codebase follows a clear feature-by-layer organization inside `app/src/main/java/com/kutubuddin/sabeel/`:

- `/data` - Concrete repository implementations, Room database configuration (DAOs, Entities), and DataStore preferences.
- `/di` - Dagger Hilt modules (`DataModule`, `RepositoryModule`, `DispatchersModule`, etc.) for dependency injection.
- `/domain` - Pure business logic interfaces, domain models (e.g., `DhikrCatalog`), and repository contracts.
- `/ui` - Jetpack Compose screens, ViewModels, and UI state categorized by feature (`/home`, `/tasbih`, `/dhikr`, `/settings`, `/navigation`, `/theme`).

## Key Features

- **Theological Foundation:** Over 35 authentic dhikr entries sourced from Sahih al-Bukhari, Sahih Muslim, Sunan Abu Dawud, and Jami' at-Tirmidhi.
- **Circle-Centric Counting:** A glowing gold circle acts as a massive tap target with spring-physics animation and precision haptic feedback.
- **Smart Flow (Post-Salah Engine):** Automatically sequences post-prayer dhikr (e.g., SubhanAllah → Alhamdulillah → Allahu Akbar) silently transitioning between milestones.
- **Home Dashboard:** Tracks daily streaks, goals, and maintains session history.
- **Dhikr Library:** An expandable, searchable catalog including an immersive Asma Ul Husna (99 Names of Allah) gallery.

## Development Workflow

The development workflow is heavily structured around feature branches and pull requests to maintain stability:

1. Create a feature branch off of `dev`: `git checkout -b feature/your-feature-name`
2. Make your atomic changes.
3. Commit messages must follow [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `refactor:`, `chore:`.
4. Run all tests locally. Ensure `./gradlew :app:assembleDebug` passes.
5. Push your branch and open a Pull Request against the `dev` branch for review.

## Coding Standards

- **MVI Contracts:** All UI states must be immutable (`State`). User actions are mapped to `Intent`s, and one-shot events are mapped to `SideEffect`s.
- **Threading Model:** The UI thread must never be blocked. All IO operations (Room database queries, DataStore writes) must be dispatched to `Dispatchers.IO` via the `@IoDispatcher` qualifier injected by Hilt.
- **Design System ("Mushaf Night"):** Use predefined semantic tokens (e.g., `Background` for true OLED black, `GoldPrimary` for milestones, `ArabicText` for scripts). Do not use hardcoded hex values in the UI layer.

## Testing

Testing is heavily emphasized to ensure stability for daily users. The project utilizes JUnit 4, MockK, Robolectric, and Compose UI Test frameworks.

- **Run Unit Tests:** `./gradlew :app:testDebugUnitTest`
- **Run Instrumented Tests:** `./gradlew :app:connectedDebugAndroidTest` (requires connected device/emulator)

## Contributing

Contributions are welcome! Please open an issue before submitting a pull request so the approach can be discussed. If you notice any theological inaccuracy or typo in the Arabic script within `DhikrCatalog.kt`, please open an issue with the correct Hadith reference.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for more details.
