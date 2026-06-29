# Product Requirements Document: "Sabeel"

## 1. Product Vision & Core Philosophy
**Sabeel** is a premium, ultra-tactile Android application designed to facilitate daily Islamic *Dhikr* (remembrance) for busy individuals. 

**Core Philosophy:**
*   **Eyes-Free & Distraction-Free:** Designed primarily for use while commuting, walking, or resting. Users should be able to count flawlessly without ever looking at the screen, relying entirely on advanced haptic feedback and physical button integrations.
*   **Tactile & Premium:** The app must feel like a native, high-end hardware extension. Animations should mirror real-world physics, and haptics should emulate crisp physical mechanisms (similar to the Apple Taptic Engine experience).
*   **Theologically Authentic:** Rooted strictly in authentic Sunnah, providing exact, verified targets and authentic Arabic typography for those who choose to engage visually.

---

## 2. Theological Framework
The app targets are strictly derived from authentic Hadith to ensure users are engaging in highly rewarded practices.

### A. Authentic Dhikr Targets
1.  **Seek Forgiveness (Astaghfirullah) – 100x Daily**
    *   *Reference:* "O people, repent to Allah and seek His forgiveness, for I repent to Him one hundred times a day." (Sahih Muslim)
2.  **SubhanAllahi wa bihamdihi – 100x Daily**
    *   *Reference:* Forgives sins even if as much as the foam of the sea. (Sahih al-Bukhari 6405)
3.  **The Tahlil – 100x Daily**
    *   *Dhikr:* "La ilaha illallahu, wahdahu la sharika lahu..." 
    *   *Reference:* Reward of freeing 10 slaves, 100 good deeds, and protection from the devil. (Sahih al-Bukhari 3293)
4.  **Post-Salah & Evening Tasbih**
    *   *Variation 1:* 33x Subhan-Allah, 33x Al-hamdu lillah, 34x Allahu Akbar. (Sahih Muslim 596)
    *   *Variation 2:* 33x each, completing the 100th with the Tahlil.

### B. Tajweed & Typography Standards
*   **Font Standard:** Must use the **KFGQPC Uthmanic Script (Hafs)** for accurate representation of the Mushaf.
*   **Audio-Synchronized Highlighting:** Transliterations are discouraged. Instead, teach pronunciation via audio-synchronized syllable highlighting.
*   **Performance Constraint:** Do not use standard state-rebuilding loops for millisecond tracking. Use Compose's `Animatable` or independent canvas painters to style the active text span without triggering full widget-tree rebuilds at 60fps.

---

## 3. Core Feature Specifications

### A. Full-Screen Gesture Area
The entire screen serves as a monolithic tap target. 
*   **Interaction:** Tapping anywhere increments the counter.
*   **Visual Feedback:** The tap location triggers a fluid, spring-physics compression (scale down) that snaps back with an expressive overshoot.

### B. Volume Key Pocket Mode
For true eyes-free commuting, users can count while the phone is asleep in their pocket.
*   **Interaction:** Pressing Volume Up or Volume Down increments the counter.
*   **Execution:** Accomplished via a Foreground Service spoofing a Media Session, intercepting volume commands without changing the actual device media volume.

### C. Smart Haptic Engine
Standard vibrations are insufficient. The app will utilize a custom haptic composition map:
*   **Count (TICK):** A very light, short tap for every standard increment.
*   **Milestone (CLICK):** A sharp, distinct pulse when reaching 33, 66, or 99.
*   **Completion/Reset (THUD):** A heavy, low-frequency resonance when a 100x target is completed or when the user long-presses to reset.

### D. Smart Flow (Auto-transitioning Post-Prayer Dhikr)
When selecting a compound Dhikr (e.g., Post-Salah 33/33/34), the app automatically transitions the on-screen Arabic text and audio context to the next phrase (SubhanAllah → Alhamdulillah → Allahu Akbar) seamlessly upon hitting the milestone, accompanied by a distinct haptic signature to notify the eyes-free user.

---

## 4. Technical Stack & Architecture

### A. UI/UX Architecture
*   **Framework:** Native Android with **Jetpack Compose**.
*   **Animation System:** Material 3 `MotionScheme.expressive()`.
*   **Implementation:** Replace duration-based Tweens with `fastSpatialSpec()` and `defaultSpatialSpec()` spring physics for all spatial UI transformations.

### B. Haptics Engine Integration
*   **API:** `VibratorManager` and `VibrationEffect.Composition`.
*   **Primitives:** Utilize hardware-level linear motor primitives (`PRIMITIVE_CLICK`, `PRIMITIVE_TICK`, `PRIMITIVE_THUD`). Implement a graceful fallback (`HapticFeedbackConstants`) for older Android hardware lacking advanced actuators.

### C. Hardware Integration Pattern
*   **Service:** A lightweight Foreground Service (with a persistent "Tasbih Active" notification).
*   **Audio Focus & Interception:** Instantiate `MediaSessionCompat` and attach a `VolumeProviderCompat(VOLUME_CONTROL_RELATIVE)`. This strictly avoids the heavy permission requirements of Accessibility Services while ensuring reliable background execution.

### D. Storage Strategy
*   **Active Counter:** **Jetpack DataStore** (Preferences/Proto). Heavily optimized via Kotlin Coroutines/Flow for rapid, concurrent integer increments without main-thread blocking.
*   **Analytics/History:** **Room Database**. Used strictly for saving daily totals, historical charts, and timestamps (avoided for the live counting loop due to relational I/O overhead).

### E. SOLID Architecture Principles
The architecture will enforce SOLID principles to ensure maintainability and scalability:
*   **Single Responsibility Principle (SRP):** Repositories, ViewModels, and UI components will each have a single responsibility. For example, `HapticEngine` handles only vibration sequences, separating hardware logic from the UI.
*   **Open/Closed Principle (OCP):** Core tracking components will be open for extension but closed for modification. For example, adding new Dhikr targets will extend a base `DhikrTarget` interface without altering the core counting engine.
*   **Liskov Substitution Principle (LSP):** Concrete implementations of data sources (e.g., `LocalDataStore` vs. `RoomDataSource`) will be seamlessly substitutable within the Repository layer.
*   **Interface Segregation Principle (ISP):** Interfaces will be small and specific. A `VibrationController` interface will be distinct from an `AudioController` interface.
*   **Dependency Inversion Principle (DIP):** High-level modules (ViewModels) will depend on abstractions (Repository interfaces), not concrete implementations, facilitated by a dependency injection framework like **Hilt**.

---

## 5. Non-Functional Requirements

### A. Battery Optimization & OLED True-Black
*   **Power Draw Minimization:** The active counting screen must use absolute OLED black (`#000000`) for the `background` and `surface` colors, effectively turning off the pixels to achieve near-zero screen power consumption during active use.
*   **Smear Mitigation:** To prevent OLED purple smearing (ghosting during scrolling/animation over black), animated UI elements must possess high-contrast borders rather than soft dark-gray gradients.

### B. Performance & Accessibility
*   **Frame Rates:** All counting animations and audio-sync highlights must maintain a rigid 60fps/120fps (device dependent).
*   **Accessibility:** Fully compatible with Android TalkBack. However, when Pocket Mode is active, verbal screen-reader counting should optionally be suppressed in favor of haptics to maintain the discrete nature of the commuter experience.
