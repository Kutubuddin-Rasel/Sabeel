# Architecture Analysis & Recommendation Report: Sabeel (Tasbih)

This report details the architectural analysis and recommended modifications for the **Sprint 0 & Foundational Architecture** milestone of the Sabeel Android application. 

---

## 1. Codebase Structure & Build Configurations

The Sabeel codebase is organized as a single-module Android Gradle project:
- **Core Package Directory**: `app/src/main/java/com/kutubuddin/sabeel`
  - Contains `MainActivity.kt` (entry-point `ComponentActivity` using Jetpack Compose).
  - Contains `ui/theme` package with `Color.kt`, `Theme.kt`, and `Type.kt`.
- **Resources**: `app/src/main/res` (contains launcher icons, default themes, etc.).
- **Build System**: Kotlin DSL (`.gradle.kts`) with Gradle version catalog (`libs.versions.toml`).
  - Target/Compile SDK: **API 36**
  - Minimum SDK: **API 24**
  - Kotlin version: **2.2.10**
  - Android Gradle Plugin: **9.2.1**
  - Kotlin Compose Compiler Plugin: Enabled via `alias(libs.plugins.kotlin.compose)`

---

## 2. Recommended Dependency Injection Modules Path & Package Structure

Following Clean Architecture and SOLID principles, all Dependency Injection (DI) modules must be isolated within a central `di` package. We recommend placing the new DI modules at the following exact paths:

- **Package Name**: `com.kutubuddin.sabeel.di`
- **File System Paths**:
  - `app/src/main/java/com/kutubuddin/sabeel/di/HardwareModule.kt` (vibration/haptics wrapper bindings)
  - `app/src/main/java/com/kutubuddin/sabeel/di/DataModule.kt` (Room Database, DAOs, and DataStore instances)

---

## 3. Required 2026 Dependency Versions

To ensure compatibility with **Kotlin 2.2.10** and target **Android API 36** in 2026, the following stable dependency versions must be added:

1. **Dagger Hilt**: `2.55` (fully supports Kotlin 2.2.x annotation processing).
2. **KSP (Kotlin Symbol Processing)**: `2.2.10-1.0.31` (must match Kotlin `2.2.10` exactly).
3. **Jetpack Compose BOM**: `2026.02.01` (already declared in the project, manages stable UI libraries).
4. **Room**: `2.7.0` (native KSP compilation, Kotlin Coroutines flow support, and optimized SQLite paging).
5. **DataStore**: `1.1.2` (robust, thread-safe asynchronous preference persistence).
6. **Material 3 (M3)**: Version is managed transitively via Compose BOM `2026.02.01` (corresponds to Material 3 `1.4.0` or `1.3.1`).

---

## 4. Planned Build & Gradle Configurations

### 4.1. File: `gradle/libs.versions.toml`
Add the following version definitions, libraries, and plugins in their respective blocks:

```toml
[versions]
# Existing versions...
agp = "9.2.1"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.6.1"
activityCompose = "1.8.0"
kotlin = "2.2.10"
composeBom = "2026.02.01"

# 2026 New Additions
hilt = "2.55"
ksp = "2.2.10-1.0.31"
room = "2.7.0"
datastore = "1.1.2"

[libraries]
# Existing libraries...
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }

# 2026 Hilt Additions
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }

# 2026 Room Additions
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# 2026 DataStore Additions
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

[plugins]
# Existing plugins...
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }

# 2026 New Additions
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

### 4.2. File: `build.gradle.kts` (Project-level)
Declare Hilt and KSP plugins in the top-level plugins block without applying them:

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

### 4.3. File: `app/build.gradle.kts` (App-level)
Apply KSP, Hilt, configure compilation options for Java 17 (mandatory for Hilt 2.50+ / Room 2.7.0), and add dependency rules.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.kutubuddin.sabeel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kutubuddin.sabeel"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Required upgrade to Java 17 toolchain for modern DI & Room
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // Hilt Dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Room Dependencies
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    // DataStore Dependencies
    implementation(libs.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

---

## 5. Detailed Design Recommendations

### 5.1. VibratorManager Wrapper (Dependency Inversion & Interface Segregation)

To enforce **Interface Segregation Principle (ISP)** and **Dependency Inversion Principle (DIP)**:
1. We define a highly specialized domain-level interface `TasbihHapticController` containing only methods relevant to the Dhikr session counting logic (segregated from general system haptics).
2. The UI layer/ViewModel interacts strictly with this abstraction.
3. The concrete implementation resides in the hardware/data package and utilizes Android’s `Vibrator` system (utilizing `VibratorManager` on S+).

#### Interface Definition (`domain/haptic/TasbihHapticController.kt`)
```kotlin
package com.kutubuddin.sabeel.domain.haptic

interface TasbihHapticController {
    /** Triggers a very light, short haptic tick for a standard counter increment. */
    fun playIncrementTick()

    /** Triggers a sharp, distinct haptic click upon reaching milestones (33, 66, 99). */
    fun playMilestoneClick()

    /** Triggers a heavy, low-frequency haptic thud upon completion (100) or manual reset. */
    fun playCompletionThud()
}
```

#### Concrete Hardware Implementation (`data/hardware/AndroidTasbihHapticController.kt`)
```kotlin
package com.kutubuddin.sabeel.data.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.kutubuddin.sabeel.domain.haptic.TasbihHapticController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTasbihHapticController @Inject constructor(
    private val vibrator: Vibrator
) : TasbihHapticController {

    override fun playIncrementTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibratePrimitiveIfSupported(
                primitiveId = VibrationEffect.Composition.PRIMITIVE_TICK,
                fallbackDuration = 10,
                fallbackAmplitude = 80
            )
        } else {
            vibrateLegacy(duration = 10, amplitude = 80)
        }
    }

    override fun playMilestoneClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibratePrimitiveIfSupported(
                primitiveId = VibrationEffect.Composition.PRIMITIVE_CLICK,
                fallbackDuration = 25,
                fallbackAmplitude = 180
            )
        } else {
            vibrateLegacy(duration = 25, amplitude = 180)
        }
    }

    override fun playCompletionThud() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibratePrimitiveIfSupported(
                primitiveId = VibrationEffect.Composition.PRIMITIVE_THUD,
                fallbackDuration = 80,
                fallbackAmplitude = 255
            )
        } else {
            vibrateLegacy(duration = 80, amplitude = 255)
        }
    }

    private fun vibratePrimitiveIfSupported(primitiveId: Int, fallbackDuration: Long, fallbackAmplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && vibrator.arePrimitivesSupported(primitiveId)) {
            val composition = VibrationEffect.startComposition()
                .addPrimitive(primitiveId, 1.0f)
                .compose()
            vibrator.vibrate(composition)
        } else {
            vibrateLegacy(fallbackDuration, fallbackAmplitude)
        }
    }

    private fun vibrateLegacy(duration: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}
```

#### DI Module binding (`di/HardwareModule.kt`)
```kotlin
package com.kutubuddin.sabeel.di

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import com.kutubuddin.sabeel.data.hardware.AndroidTasbihHapticController
import com.kutubuddin.sabeel.domain.haptic.TasbihHapticController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindTasbihHapticController(
        impl: AndroidTasbihHapticController
    ): TasbihHapticController
}

@Module
@InstallIn(SingletonComponent::class)
object SystemServiceModule {

    @Provides
    @Singleton
    fun provideVibrator(@ApplicationContext context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
}
```

---

### 5.2. Room and DataStore Configuration

The persistent architecture strictly divides responsibilities based on performance profiles:
1. **Jetpack DataStore (Preferences)** handles the *active* live counting session state. High-frequency changes are saved in memory and serialized to disk asynchronously to prevent I/O bottlenecks.
2. **Room Database** persists daily *completed* targets and streaks for historical analytics, avoiding disk overhead during the rapid-tap counting loop.

#### DataStore Manager (`data/local/datastore/CounterDataStore.kt`)
```kotlin
package com.kutubuddin.sabeel.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val keyCount = intPreferencesKey("active_count")
    private val keyDhikrType = stringPreferencesKey("active_dhikr_type")
    private val keyTarget = intPreferencesKey("active_target")

    val activeCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[keyCount] ?: 0
    }

    val activeDhikrType: Flow<String> = dataStore.data.map { preferences ->
        preferences[keyDhikrType] ?: "SUBHANALLAH"
    }

    val activeTarget: Flow<Int> = dataStore.data.map { preferences ->
        preferences[keyTarget] ?: 33
    }

    suspend fun incrementCount() {
        dataStore.edit { preferences ->
            val current = preferences[keyCount] ?: 0
            preferences[keyCount] = current + 1
        }
    }

    suspend fun resetCount() {
        dataStore.edit { preferences ->
            preferences[keyCount] = 0
        }
    }

    suspend fun setDhikr(dhikrType: String, targetCount: Int) {
        dataStore.edit { preferences ->
            preferences[keyDhikrType] = dhikrType
            preferences[keyTarget] = targetCount
            preferences[keyCount] = 0
        }
    }
}
```

#### DI Module (`di/DataModule.kt`)
```kotlin
package com.kutubuddin.sabeel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kutubuddin.sabeel.data.local.db.SabeelDatabase
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sabeel_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SabeelDatabase {
        return Room.databaseBuilder(
            context,
            SabeelDatabase::class.java,
            "sabeel_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSakinahDao(database: SabeelDatabase): SakinahDao {
        return database.sakinahDao()
    }
}
```

---

### 5.3. Compose OLED Black Theme Setup

To conform with the strict power optimization constraints:
1. **Absolute Black Scheme:** The primary surfaces and background colors must be assigned `#000000` (pure black). This completely shuts off OLED pixels, minimizing battery drain.
2. **Globally Disabling Tonal Elevations:** Material 3 automatically applies a tint overlay to surface components when elevated. This defeats the OLED black styling. By overriding the Compose `LocalAbsoluteTonalElevation` CompositionLocal to `0.dp`, we disable this behavior across all Material components globally.
3. **Eliminating Shadows:** Standard shadows (`shadowElevation`) are disabled. Instead of relying on shadows, we separate layout items purely via structural negative space, padding, and strong contrast in typography hierarchy (varying font-sizes, weights, and high-contrast color choices like white vs. mid-gray `#888888`).
4. **OLED Smear Mitigation (No 10% White Borders):** Low-opacity gray or 10% translucent white borders can cause visual "purple smearing" on OLED displays when animated (due to slow pixel response times transitioning between off and dim gray). Instead of soft gray borders:
   - Static layout content resides directly on the black canvas with no containers or borders.
   - For active, animated components (e.g. the tap button or rolling odometer), we use thin, high-contrast, fully opaque borders matching the theme’s primary or secondary solid accent colors (e.g., solid `#D0BCFF` or `#FFFFFF`). This forces the active pixels to transition immediately from pure black to high brightness, bypassing the low-brightness gray state that triggers the smear effect.

#### Refactored Theme File (`ui/theme/Theme.kt`)
```kotlin
package com.kutubuddin.sabeel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Custom High-Contrast True OLED Dark Color Scheme
private val OledDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),     // Vibrant primary accent
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    background = Color.Black,        // #000000 Absolute Black
    surface = Color.Black,           // #000000 Absolute Black
    surfaceVariant = Color.Black,    // #000000 Absolute Black
    onBackground = Color.White,      // Crisp white readability
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0xFF444444),     // Sharp outline boundary
    outlineVariant = Color(0xFF222222)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun SabeelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Force OledDarkColorScheme in dark mode (and disable dynamic color to preserve absolute black styling)
    val colorScheme = if (darkTheme) OledDarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        // Globally disable M3 tonal elevation (retains absolute black on cards/sheets)
        LocalAbsoluteTonalElevation provides 0.dp
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
```
