# Original User Request

## Initial Request — 2026-06-29T23:07:11+06:00

# Teamwork Project Prompt — Draft

> Status: Launched
> Goal: Craft prompt → get user approval → delegate to teamwork_preview

Sabeel: A premium, eyes-free Android application. This step focuses on Step 1: Sprint 0 & Foundational Architecture, outputting the structural codebase following SOLID principles.

Working directory: /Users/kutubuddin/Downloads/Sabeel
Integrity mode: development

## Requirements

### R1. Dependency Management (Build & Infrastructure)
Generate the `libs.versions.toml` file configuring 2026 dependencies for Dagger Hilt, Jetpack Compose BOM, Room Database, Jetpack DataStore, and modern Compose Material 3 components for expressive motion.
Provide corresponding `build.gradle.kts` (Project and Module) configuration snippets to apply Hilt and KSP plugins.

### R2. Dependency Injection Matrix (Software Architecture)
Create `HardwareModule.kt` providing a singleton wrapper for the `VibratorManager` system service.
Create `DataModule.kt` providing Room database initialization and Jetpack DataStore active counter configuration.
All providers must return small, specific interfaces conforming to the Interface Segregation Principle.

### R3. UI System Design Tokens (UI Design)
Generate Compose `Theme.kt` and `Color.kt` files.
Define true OLED black (`#000000`) as the absolute base color for `background` and `surface`.
Implement Tonal Elevation or explicit `Color.White.copy(alpha = 0.1f)` borders instead of legacy shadow elevations to mitigate OLED purple smearing.

### R4. Code Synthesis
Compile and output the completed files (`libs.versions.toml`, `build.gradle.kts` setup snippets, Hilt DI Modules, and Compose Theme configuration files) as raw, production-ready code.

## Acceptance Criteria

### Code Generation & Structure
- [ ] `libs.versions.toml` is created with all specified dependencies.
- [ ] `build.gradle.kts` snippets for Hilt and KSP are provided.
- [ ] `HardwareModule.kt` is created and provides an interface wrapper for `VibratorManager`.
- [ ] `DataModule.kt` is created and provides Room and DataStore configurations.
- [ ] `Theme.kt` and `Color.kt` are created, explicitly defining `#000000` for background/surface and omitting standard shadows in favor of tonal elevation/borders.
- [ ] All code conforms to SOLID principles (specifically DIP and ISP as requested).
