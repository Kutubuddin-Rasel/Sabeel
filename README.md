<div align="center">

<h1>سَبِيل — Sabeel</h1>

<p><strong>A premium digital Tasbih centered on the circle — designed for the modern Muslim.</strong></p>

<p>
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" alt="Platform" />
  <img src="https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen?logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Architecture-MVI%20%2B%20Clean-orange" alt="Architecture" />
  <img src="https://img.shields.io/badge/Version-1.0.0-lightgrey" alt="Version" />
</p>

</div>

---

## 📖 What Is Sabeel?

**Sabeel** (Arabic: سَبِيل — *the path, the way*) is a distraction-free Android application designed to help Muslims perform **dhikr** (remembrance of Allah) consistently throughout their day — especially during commuting, walking, or any moment when their hands are free but their eyes are not.

The core philosophy is **"Invisible Interface"**: the phone becomes a digital prayer bead (tasbih) that requires zero visual attention. A single tap anywhere on the screen counts. Precise haptic feedback tells you exactly where you are in a session without ever looking at the screen.

> *"Whoever says SubhanAllah 33 times, Alhamdulillah 33 times, and Allahu Akbar 34 times after each prayer — that is 100 total..."*
> — Sahih Muslim 596

---

## ✨ Key Features

### 🕌 Theological Foundation
- **35+ authentic dhikr** catalog sourced from Sahih Bukhari, Sahih Muslim, Abu Dawud, and Tirmidhi
- Every entry includes Arabic text, transliteration, meaning (English / اردو / বাংলা), spiritual reward, and Hadith reference
- **KFGQPC Uthmanic Script Hafs** font — the same script used in the King Fahd Quran Complex edition

### 👆 Circle-Centric Counting
- Tap the **glowing gold circle** — the single, clear tap target at the heart of the screen
- The circle presses in with **spring-physics animation** on every tap, giving satisfying physical feedback
- Full-screen tap also works as an **eyes-free / accessibility fallback** so you never need to aim
- **Long-press** the circle (or anywhere) to **reset** the current count to zero
- **−1 pill** in the bottom-left corner for quick decrement / undo
- Precision haptic feedback: soft tick on each count, crisp click on milestones, deep thud on completion
- Full **Pocket Mode** — counts in your pocket without lighting up the screen

### ⚡ Smart Flow (Post-Salah Dhikr Engine)
Automatically sequences the post-prayer dhikr for you:
- **Variation 1 (Classic):** SubhanAllah × 33 → Alhamdulillah × 33 → Allahu Akbar × 34
- **Variation 2 (With Tahlil):** SubhanAllah × 33 → Alhamdulillah × 33 → Allahu Akbar × 33 → La ilaha illallah × 1

On reaching each milestone, the app silently transitions to the next dhikr — no tap needed.

### 📊 Home Dashboard
- **Daily streak** tracking with longest streak record
- **Daily goal** progress bar (configurable, default 200 counts)
- **Resume session** card — pick up exactly where you left off
- Session history for the current day
- All-time total count and session count

### 📚 Dhikr Library
- Full catalog with **category sticky headers** (After Prayer, Daily, Morning, Evening, Salawat, Istighfar, Tahlil, My Dhikr)
- **Live search** with Arabic, transliteration, and English matching
- Expandable cards showing full Arabic text, transliteration, meaning, spiritual reward, and Hadith reference
- **"Count Now"** button — instantly opens the counting screen pre-loaded with your selected dhikr

### ⚙️ Settings
- **Theme:** Dark (OLED true-black) / Light
- **Translation language:** English / اردو / বাংলা
- **Haptic feedback level:** Off / Light / Medium / Strong
- **Transliteration** toggle
- **Auto-reset on completion** toggle
- **Sound on milestone** toggle
- **Daily goal** stepper (50-step increments)

---

## 🏗️ Architecture

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
│   custom dhikr)   │   settings, pocket mode)            │
└─────────────────────────────────────────────────────────┘
```

### MVI Contract (TasbihViewModel example)

| Type | Description |
|---|---|
| `TasbihState` | Immutable snapshot of the counting screen (count, target, dhikr, streak, …) |
| `TasbihIntent` | User actions: `Increment`, `Decrement`, `Reset`, `SetDhikr`, `SetPocketModeActive`, … |
| `TasbihSideEffect` | One-shot events: `PlayHaptic`, `ShowCelebration`, `StartPocketModeService`, … |

### Threading Model

All IO operations are dispatched to `Dispatchers.IO` via a `@IoDispatcher`-qualified `CoroutineDispatcher` injected by Hilt. The UI thread is **never blocked**.

- **DataStore** — live counter, settings (async Preferences DataStore)
- **Room** — sessions, streaks, custom dhikr (suspend functions + Flow)
- **Mutex-guarded repository writes** — prevent race conditions during rapid tapping

---

## 📂 Project Structure

```
app/src/main/java/com/kutubuddin/sabeel/
│
├── data/
│   ├── local/
│   │   ├── datastore/       # CounterDataStore (live Preferences DataStore)
│   │   └── db/
│   │       ├── dao/         # SakinahDao, DhikrSessionDao, CustomDhikrDao
│   │       ├── entity/      # DailyTargetEntity, StreakEntity,
│   │       │                # DhikrSessionEntity, CustomDhikrEntity
│   │       └── SabeelDatabase.kt  # Room v2, MIGRATION_1_2
│   └── repository/          # Concrete implementations
│       ├── TasbihRepositoryImpl.kt
│       ├── SessionRepositoryImpl.kt
│       ├── DhikrRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
│
├── di/                      # Hilt modules
│   ├── DataModule.kt        # Room, DataStore providers
│   ├── RepositoryModule.kt  # Abstract bindings (DIP)
│   ├── DispatchersModule.kt # @IoDispatcher qualifier
│   ├── HardwareModule.kt    # HapticEngine
│   └── SystemServiceModule.kt
│
├── domain/
│   ├── model/               # DhikrType, DhikrItem, DhikrCatalog (35+ entries)
│   │                        # DhikrCategory, DhikrMeaning, Streak, DailyTarget
│   ├── repository/          # Pure interfaces (DIP)
│   │   ├── TasbihRepository.kt
│   │   ├── SessionRepository.kt
│   │   ├── DhikrRepository.kt
│   │   └── SettingsRepository.kt
│   └── haptic/              # HapticEngine interface
│
├── service/
│   └── PocketModeService.kt # Foreground service for in-pocket counting
│
└── ui/
    ├── home/                # HomeScreen, HomeViewModel, HomeState
    ├── tasbih/              # TasbihScreen, TasbihViewModel, TasbihCircle,
    │                        # TajweedText, SpiritualRewardCard, OdometerCounter
    ├── dhikr/               # DhikrLibraryScreen, DhikrViewModel, DhikrLibraryState
    ├── settings/            # SettingsScreen, SettingsViewModel, SettingsState
    ├── navigation/          # SabeelNavHost, SabeelBottomBar, SabeelTab
    └── theme/               # SabeelColors, Typography, SabeelTheme
```

---

## 🎨 Design System — "Mushaf Night"

The visual design is inspired by illuminated Quranic manuscripts: **antique gold calligraphy on an infinite black void**.

| Token | Value | Usage |
|---|---|---|
| `Background` | `#000000` | True OLED black — zero pixel emission |
| `Surface` | `#0D0D0D` | Cards, sheets |
| `GoldPrimary` | `#C9A84C` | Active states, title, arc fill |
| `GoldLuminous` | `#E8C547` | Milestone flash, arc end |
| `ArabicText` | `#F0F0F0` | All Arabic dhikr text |
| `TextPrimary` | `#D4D4D4` | English labels, display names |
| `StreakAmber` | `#FF8C42` | Streak fire indicator |
| `SageGreen` | `#5A7A5A` | Spiritual reward text |

**Typography:** KFGQPC Uthmanic Script Hafs (Arabic) + system defaults (Latin)

---

## 🛠️ Tech Stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin | 2.4.x |
| UI | Jetpack Compose + Material 3 | BOM latest |
| DI | Dagger Hilt | 2.x |
| Database | Room | 2.x |
| Preferences | DataStore Preferences | 1.x |
| Navigation | Navigation Compose | 2.x |
| Code Gen | KSP | 2.x |
| Graphics | `androidx.graphics.shapes` | — |
| Build | Gradle 9 + KTS | — |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | API 36 | — |
| Java | JVM 17 | — |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Meerkat (or later)
- **JDK 17**
- Android device or emulator running **Android 7.0+ (API 24)**

### Clone & Build

```bash
# Clone the repository
git clone https://github.com/kutubuddin-rasel/sabeel.git
cd sabeel

# Build debug APK
./gradlew :app:assembleDebug

# Install on connected device
./gradlew :app:installDebug
```

### Font Setup

The KFGQPC Uthmanic Script Hafs font file is included at the project root.

```
Sabeel/
└── KFGQPC Uthmanic Script HAFS Regular.otf   ← already included
```

It is referenced in `UthmanicHafsFontFamily` inside the `ui/theme/` package.

---

## 🧪 Testing

```bash
# Unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests (requires connected device/emulator)
./gradlew :app:connectedDebugAndroidTest
```

Test dependencies: **JUnit 4**, **MockK**, **Robolectric**, **kotlinx-coroutines-test**, **Compose UI Test**.

---

## 🗺️ Roadmap

| Status | Feature |
|---|---|
| ✅ | 4-Tab Navigation (Home, Count, Dhikr, Settings) |
| ✅ | Smart Flow post-Salah engine (2 variations) |
| ✅ | Pocket Mode (foreground service) |
| ✅ | 35+ Hadith-sourced dhikr catalog |
| ✅ | Daily streak + goal tracking |
| ✅ | Multi-language translation (EN / UR / BN) |
| ✅ | Room v2 database with safe migration |
| ✅ | Session history on Home tab |
| 🔜 | Audio-synchronized highlighting |
| 🔜 | Light theme |
| 🔜 | Custom dhikr creation |
| 🔜 | Widget for Home Screen |
| 🔜 | Salah time-aware automatic suggestions |
| 🔜 | Google Play release |

---

## 🤝 Contributing

Contributions are welcome! Please open an issue before submitting a pull request so the approach can be discussed.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit following [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `refactor:`
4. Push and open a Pull Request against `dev`

Please ensure `./gradlew :app:assembleDebug` passes before opening a PR.

---

## 📜 Islamic Notes

All dhikr entries in `DhikrCatalog.kt` are sourced from authenticated Hadith collections:

- **Sahih al-Bukhari** (صحيح البخاري)
- **Sahih Muslim** (صحيح مسلم)
- **Sunan Abu Dawud** (سنن أبي داود)
- **Jami' at-Tirmidhi** (جامع الترمذي)

If you notice any theological inaccuracy in an entry, please open an issue with the correct reference.

---

## 📄 License

```
Copyright 2026 Kutubuddin Rasel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<div align="center">
<p><em>May this tool be a means of ease on the path of dhikr.</em></p>
<p><strong>بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ</strong></p>
</div>
