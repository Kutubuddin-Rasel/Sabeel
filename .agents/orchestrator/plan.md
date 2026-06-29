# Sabeel Sprint 0 & Foundational Architecture Plan

## Scope
Set up dependency management, DI modules, database & preferences configuration, and the design theme for the Sabeel Android application.

## Milestones & Tasks
1. **Milestone 1: Project Setup & Dependency Configuration**
   - Create `gradle/libs.versions.toml` with 2026 versions of Dagger Hilt, Compose BOM, Room, DataStore, and Material 3.
   - Update `build.gradle.kts` (Project and Module) to apply Hilt and KSP plugins.
2. **Milestone 2: Hardware & Data Layer Architecture (DI)**
   - Create `HardwareModule.kt` containing the `VibratorManager` wrapper following DIP and ISP.
   - Create `DataModule.kt` providing Room Database and DataStore configurations following DIP and ISP.
3. **Milestone 3: UI Theme & Design Tokens**
   - Create `Theme.kt` and `Color.kt` with OLED black (`#000000`) background/surface and tonal elevations / borders to mitigate OLED purple smearing.
4. **Milestone 4: Verification & Handoff**
   - Run compilation and verify Hilt dependency injection compiles properly.
   - Deliver handoff to parent Sentinel agent.

## Execution Strategy
- Dispatch read-only exploration to investigate existing codebase and layout via `teamwork_preview_explorer` (using LeanKG first).
- Dispatch implementation tasks to `teamwork_preview_worker`.
- Run reviews via `teamwork_preview_reviewer`.
- Run final checks via `teamwork_preview_auditor`.
