# Technical Design Document: "Sabeel" (The Commuter's Tasbih)

## 1. System Architecture Overview
The architecture is designed around the **MVI (Model-View-Intent)** paradigm, utilizing modern Android capabilities (targeting mid-2026). The system guarantees atomic, thread-safe counter increments during rapid physical or screen-based interactions. The application is divided into robust, independent layers:
*   **State Management:** Kotlin Coroutines and `MutableStateFlow` enforce a single immutable source of truth.
*   **UI Rendering:** Jetpack Compose, optimized for OLED true-black rendering and Expressive MotionScheme physics.
*   **Data Persistence:** Room Database with structured concurrency for historical tracking, paired with Jetpack DataStore for rapid active counting.
*   **Hardware Integration:** A background MediaSession-based Foreground Service intercepting volume keys, paired with `VibratorManager` composition for ultra-crisp tactile feedback.

---

## 2. Data Layer (Room DB Schemas)
Structured concurrency ties database operations to the `viewModelScope`. Read operations rely on reactive `Flow` collections, while atomic updates utilize `@Transaction` inside `suspend` functions offloaded to `Dispatchers.IO`.

### 2.1 Entity Definitions
```kotlin
@Entity(tableName = "daily_targets")
data class DailyTargetEntity(
    @PrimaryKey val id: String, // Format: "{DHIKR_TYPE}_{YYYY-MM-DD}"
    val date: String, // ISO-8601
    val dhikrType: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean
)

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val id: Int = 1, // Singleton row
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: String // ISO-8601
)
```

### 2.2 DAO & Atomic Transactions
```kotlin
@Dao
interface SakinahDao {
    @Query("SELECT * FROM daily_targets WHERE date = :date")
    fun getDailyTargets(date: String): Flow<List<DailyTargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTarget(target: DailyTargetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStreak(streak: StreakEntity)

    @Query("SELECT * FROM streaks WHERE id = 1")
    suspend fun getStreakSync(): StreakEntity?

    @Transaction
    suspend fun completeTargetAndUpdateStreak(target: DailyTargetEntity, currentDate: String) {
        upsertTarget(target.copy(isCompleted = true))
        val streak = getStreakSync() ?: StreakEntity(1, 0, 0, currentDate)
        // Streak calculation logic...
        upsertStreak(streak.copy(currentStreak = newStreakCount, lastActiveDate = currentDate))
    }
}
```

---

## 3. UI Layer & State Management (StateFlow & Compose)

### 3.1 StateFlow & MVI
To prevent race conditions during rapid tapping, the ViewModel employs a strictly unidirectional MVI flow. Counter increments are executed via `MutableStateFlow.update {}`, utilizing Compare-And-Swap (CAS) to guarantee atomicity. This synchronously handles logic like the "Smart Flow" post-prayer mode without UI lag.

```kotlin
data class TasbihState(
    val currentDhikr: DhikrType = DhikrType.SUBHANALLAH,
    val count: Int = 0,
    val target: Int = 33,
    val isSessionComplete: Boolean = false
)

sealed interface TasbihIntent {
    object Increment : TasbihIntent
    object Reset : TasbihIntent
}

class TasbihViewModel : ViewModel() {
    private val _state = MutableStateFlow(TasbihState())
    val state = _state.asStateFlow()

    fun processIntent(intent: TasbihIntent) {
        when (intent) {
            is TasbihIntent.Increment -> _state.update { currentState ->
                // Atomic increment and Smart Flow transitions evaluated here synchronously
                val newCount = currentState.count + 1
                // logic to auto-transition or increment...
            }
            is TasbihIntent.Reset -> _state.value = TasbihState()
        }
    }
}
```

### 3.2 Jetpack Compose Rendering & Animation
*   **OLED True-Black Rendering:** The theme applies `#000000` strictly to the `background` and `surface` primitives. To preserve visual hierarchy and prevent OLED smearing, `shadowElevation` is replaced by Tonal Elevation or subtle borders (`Color.White.copy(alpha = 0.1f)`).
*   **Expressive MotionScheme:** Replaces legacy tween animations with spring dynamics. Applied globally via `MaterialTheme(motionScheme = MotionScheme.expressive())`.
*   **Rolling Odometer:** Utilizes `AnimatedContent` with `slideInVertically`/`slideOutVertically`. Physics are critically damped (`Spring.DampingRatioNoBouncy`, `stiffness = 1500f`) to simulate the definitive mechanical snap of a rolling odometer digit without visual overshoot.
*   **Fluid Wave Interactions:** Shape morphing between calm circles and turbulent stars leverages the `androidx.graphics.shapes.Morph` API and interpolates the morph progression using an interruptible `Animatable` (medium bounce, low stiffness).

---

## 4. Hardware Interop (Haptic Engine & Key Interception)

### 4.1 Haptic Engine (`VibratorManager`)
Custom `VibrationEffect.Composition` triggers exact actuator responses rather than mushy durations.

```kotlin
val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
val vibrator = vibratorManager.defaultVibrator

if (vibrator.arePrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK)) {
    val composition = VibrationEffect.startComposition()
        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.8f, 0)
        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 0)
        .compose()
    vibrator.vibrate(composition)
}
```

### 4.2 Key Interception (Pocket Mode)
To safely intercept volume keys when the screen is off without violating Google Play Accessibility Service policies, the system spoofes a media playback session.
*   **Architecture:** A Foreground Service encapsulates a `MediaSessionCompat` instance.
*   **Interception:** Uses `VolumeProviderCompat(VOLUME_CONTROL_RELATIVE)` to route hardware volume interrupts to the app's counting mechanism.

---

## 5. Performance & Battery Constraints

### 5.1 Eliminating Dropped Frames (State-Read Deferral)
The primary cause of jank during rapid screen-tapping is State-induced Recomposition. 
*   **Constraint:** Animated raw values must **never** be passed into modifiers that trigger layout or composition.
*   **Implementation:** Read `Animatable` state exclusively inside the draw phase via `Modifier.graphicsLayer` or `Modifier.drawBehind`. This executes UI scale/alpha translations directly on the GPU, skipping the Composition phase entirely and guaranteeing 60-120fps.

```kotlin
val morphProgress = remember { Animatable(0f) }
Box(
    modifier = Modifier.graphicsLayer {
        // Zero recomposition: read state during draw phase
        alpha = morphProgress.value
        scaleX = 1f + (morphProgress.value * 0.1f)
    }
)
```

### 5.2 Battery Management in Pocket Mode
Holding an indefinite `PARTIAL_WAKE_LOCK` for volume-key interception contradicts Android vitals and will result in OS force-kills.
*   **Constraint:** The `MediaSession` service must employ the device's `SensorManager`. The app registers a low-power step detector or proximity sensor listener to briefly acquire the `PARTIAL_WAKE_LOCK` only during physical movement. When the device is completely stationary, the lock is released, allowing deep sleep.
