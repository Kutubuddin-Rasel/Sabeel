# Project: Sabeel Android Application

## Architecture
- Root Project + Single App Module (`:app`) Structure.
- Dagger Hilt for dependency injection, Room Database for caching, DataStore for preferences/active counter.
- Jetpack Compose with custom OLED black theme and expressive motion.

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Dependency Configuration | Create libs.versions.toml and configure build.gradle.kts (Project & Module) | none | IN_PROGRESS |
| 2 | Dependency Injection Matrix | HardwareModule.kt & DataModule.kt implementation (DIP & ISP) | M1 | PLANNED |
| 3 | UI Design & Custom Theme | Theme.kt & Color.kt custom OLED black styling | M1 | PLANNED |
| 4 | Verification & Acceptance | Ensure all modules build, tests pass, and auditor approves | M1, M2, M3 | PLANNED |

## Interface Contracts
### VibratorManager Wrapper
- Interface `SabeelVibrator` providing small, specific methods (e.g. `vibrate(durationMs: Long)`, `cancel()`).
- Implementation `SystemSabeelVibrator` injecting `VibratorManager`.
- DI Provider in `HardwareModule`.

### Room & DataStore (Data Layer)
- Room database initialization with database name `sabeel-db`.
- Jetpack DataStore active counter preferences.
- DI Provider in `DataModule`.

## Code Layout
- `gradle/libs.versions.toml`
- `build.gradle.kts` (root)
- `app/build.gradle.kts`
- `app/src/main/java/com/kutubuddin/sabeel/di/HardwareModule.kt`
- `app/src/main/java/com/kutubuddin/sabeel/di/DataModule.kt`
- `app/src/main/java/com/kutubuddin/sabeel/ui/theme/Color.kt`
- `app/src/main/java/com/kutubuddin/sabeel/ui/theme/Theme.kt`
