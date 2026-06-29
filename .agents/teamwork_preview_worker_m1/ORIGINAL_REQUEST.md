## 2026-06-29T17:10:46Z
You are teamwork_preview_worker.
Your working directory is /Users/kutubuddin/Downloads/Sabeel/.agents/teamwork_preview_worker_m1/.
Your mission is to implement Step 1: Sprint 0 & Foundational Architecture for the Sabeel Android application.

DO NOT CHEAT. All implementations must be genuine. DO NOT hardcode test results, create dummy/facade implementations, or circumvent the intended task. A Forensic Auditor will independently verify your work. Integrity violations WILL be detected and your work WILL be rejected.

MANDATORY RULES:
- You MUST check LeanKG readiness via `mcp_status` first. If not ready, run `mcp_init`. Use LeanKG tools first for any codebase analysis or search.
- Compile and verify that your changes compile successfully by running `./gradlew compileDebugKotlin` or a similar build command. Report compilation results.

Please implement the following exact changes:

1. gradle/libs.versions.toml:
   Add versions:
   hilt = "2.55"
   ksp = "2.2.10-1.0.31"
   room = "2.7.0"
   datastore = "1.1.2"
   Add libraries:
   hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
   hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
   room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
   room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
   room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
   datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
   Add plugins:
   hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
   ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

2. build.gradle.kts (Project-level):
   Declare the Hilt and KSP plugins:
   alias(libs.plugins.hilt) apply false
   alias(libs.plugins.ksp) apply false

3. app/build.gradle.kts (App-level):
   Apply plugins:
   alias(libs.plugins.ksp)
   alias(libs.plugins.hilt)
   Configure compileSdk to 36 (or compileSdk = 36).
   Configure compileOptions to Java 17 (sourceCompatibility = JavaVersion.VERSION_17, targetCompatibility = JavaVersion.VERSION_17).
   Configure kotlin { jvmToolchain(17) }.
   Add dependencies for Hilt (hilt-android & hilt-compiler via ksp), Room (room-runtime, room-ktx & room-compiler via ksp), and DataStore (datastore-preferences).

4. SabeelApplication.kt:
   Create com.kutubuddin.sabeel.SabeelApplication extending android.app.Application, annotated with @HiltAndroidApp.

5. AndroidManifest.xml:
   Register .SabeelApplication under the <application> tag using android:name=".SabeelApplication".

6. Hardware Module & Haptics Wrapper (under ISP & DIP):
   - Create interface com.kutubuddin.sabeel.domain.haptic.TasbihHapticController with functions: playIncrementTick(), playMilestoneClick(), playCompletionThud().
   - Create class com.kutubuddin.sabeel.data.hardware.AndroidTasbihHapticController implementing TasbihHapticController, injecting Vibrator, and supporting modern Primitive composition (e.g. PRIMITIVE_TICK, PRIMITIVE_CLICK, PRIMITIVE_THUD) with legacy vibration fallback.
   - Create com.kutubuddin.sabeel.di.HardwareModule binding TasbihHapticController to AndroidTasbihHapticController.
   - Create SystemServiceModule providing Vibrator (supporting VIBRATOR_MANAGER_SERVICE on API 31+).

7. Room Persistence Classes:
   - Create DailyTargetEntity and StreakEntity under com.kutubuddin.sabeel.data.local.db.entity.
   - Create SakinahDao under com.kutubuddin.sabeel.data.local.db.dao (with the flow query and transaction update).
   - Create SabeelDatabase under com.kutubuddin.sabeel.data.local.db.

8. DataStore & Data DI Module:
   - Create CounterDataStore under com.kutubuddin.sabeel.data.local.datastore.
   - Create com.kutubuddin.sabeel.di.DataModule providing DataStore<Preferences>, SabeelDatabase, and SakinahDao.

9. Theme.kt & Color.kt:
   - In Color.kt, define pure OLED black Color(0xFF000000) or Color.Black.
   - In Theme.kt, define custom OledDarkColorScheme with background, surface, surfaceVariant, etc., set to Color.Black.
   - Override LocalAbsoluteTonalElevation provides 0.dp globally inside the Theme composable.
   - Omit standard shadow elevations and avoid standard translucent borders or 10% white borders.

Write a handoff report at /Users/kutubuddin/Downloads/Sabeel/.agents/teamwork_preview_worker_m1/handoff.md when complete. Send a message to me (parent conversation ID: 9fe4c52e-1382-4ffb-be5e-af02fb8300af) with build outputs and verification results.
