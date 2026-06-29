# Handoff Report: Sabeel Android Project Exploration & Foundational Architecture

## 1. Observation

Directly observed codebase configurations and structure:
- **Project Structure**: A single-module Android project containing:
  - `app/src/main/java/com/kutubuddin/sabeel/MainActivity.kt` (using `SabeelTheme` defined in `ui/theme`).
  - `app/src/main/java/com/kutubuddin/sabeel/ui/theme/Theme.kt` (lines 37-58 define `SabeelTheme` calling `MaterialTheme` with dynamic or static light/dark color schemes).
  - `app/src/main/java/com/kutubuddin/sabeel/ui/theme/Color.kt` (lines 5-11 define color constants like `Purple80` and `Purple40`).
- **Build Configurations**:
  - `app/build.gradle.kts` (lines 7-19 compile and target API 36, minSdk 24, with java compatibility 11; lines 43-48 declare compose dependencies using Compose BOM).
  - `gradle/libs.versions.toml` (lines 9-10: `kotlin = "2.2.10"`, `composeBom = "2026.02.01"`; lines 29-30 define plugins for android application and compose compiler).
- **Core Requirements Documents**:
  - `docs/sabeel_prd.md` (lines 60-76 specify dependency stack including Compose, Room, DataStore, and `VibratorManager`; lines 89-92 specify OLED black requirement).
  - `docs/sabeel_tdd.md` (lines 100-104 describe OLED black surface styling; lines 109-123 detail `VibratorManager` primitive haptic ticking; section 2 outlines Room entities and DAO).

---

## 2. Logic Chain

From the direct observations above, we establish the following chain of logic:
1. **DI Module Location**: The main package is defined as `package com.kutubuddin.sabeel` in `MainActivity.kt`. Clean Architecture and standard DI practices mandate keeping Hilt modules in a dedicated subpackage. Therefore, `di` is the logical subpackage, placing `HardwareModule.kt` and `DataModule.kt` under `com.kutubuddin.sabeel.di` (directory path `app/src/main/java/com/kutubuddin/sabeel/di`).
2. **2026 Dependency Versions**:
   - The project uses Kotlin `2.2.10`.
   - Dagger Hilt `2.55` is the compatible stable version supporting Kotlin 2.2.x annotation compilation.
   - KSP requires matching the Kotlin version exactly, leading to KSP version `2.2.10-1.0.31`.
   - Room `2.7.0` and DataStore `1.1.2` are selected as standard stable/robust versions for early 2026 Android architectures.
3. **Gradle Configurations**: Adding Hilt and KSP requires:
   - Defining Hilt, KSP, Room, and DataStore library references and plugins in `libs.versions.toml`.
   - Applying Hilt and KSP plugins in `app/build.gradle.kts` and declaring them in project `build.gradle.kts`.
   - Upgrading Java toolchain version from Java 11 to Java 17 in `app/build.gradle.kts` since modern Room (`2.7.0`) and Hilt (`2.55`) require Java 17 compilation target.
4. **VibratorManager Wrapper**:
   - Under ISP, haptic counting controls should be segregated from generic system vibrations to prevent high-level code dependency on system details.
   - Under DIP, the UI and ViewModel layer should depend on a clean interface `TasbihHapticController`, with the concrete `AndroidTasbihHapticController` implementing it and resolving OS-specific haptic primitive calls.
5. **Room & DataStore Config**:
   - The live counter state requires microsecond low-latency writes during rapid user clicks, which is perfectly suited for DataStore's thread-safe flow preferences.
   - Historical analytics and completed targets belong to Room Database transactional persistence, segregating counter activity from database disk I/O.
6. **OLED Black Theme Setup**:
   - To keep OLED pixels completely off on the active counting screen, the dark theme color scheme background and surface colors are set to `Color.Black`.
   - Material 3's built-in elevated overlay tints defeat the pure black styling. Overriding `LocalAbsoluteTonalElevation provides 0.dp` globally intercepts and disables M3 tonal elevations.
   - Dropping standard shadows completely removes legacy elevation shadows.
   - High-contrast, opaque solid outline borders (matching accent colors or solid white) are recommended only around animated elements. This forces immediate dark-to-bright pixel transitions during animation scale transitions, preventing the slow voltage-response times that cause purple OLED smearing.

---

## 3. Caveats

- We assumed that no external libraries or network access were required to verify these exact dependency version tags, since the network is in CODE_ONLY mode.
- We did not implement or compile the Gradle build changes physically, as this explorer role is strictly read-only.
- Older hardware without linear actuators (non-haptic/legacy vibration motors) will rely on the legacy `vibrateLegacy` fallback methods.

---

## 4. Conclusion

The foundational architecture can be cleanly established by placing DI modules under `com.kutubuddin.sabeel.di`, declaring matching KSP and Hilt version catalogs targeting Java 17, implementing a segregated `TasbihHapticController` using Android’s vibration primitives, dividing active/historical data between DataStore and Room, and setting up a true OLED black theme that overrides Compose’s `LocalAbsoluteTonalElevation`.

---

## 5. Verification Method

To verify these design proposals:
1. Inspect the generated report at `/Users/kutubuddin/Downloads/Sabeel/.agents/teamwork_preview_explorer_init/analysis.md` to review the proposed code changes and templates.
2. Invalidation Conditions: If Kotlin version changes from `2.2.10`, the KSP compiler version in `libs.versions.toml` must be adjusted accordingly. If Room version 2.7.0 experiences compile conflicts with minSdk 24, it can be safely downgraded to `2.6.1`.
