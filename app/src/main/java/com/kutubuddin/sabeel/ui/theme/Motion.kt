package com.kutubuddin.sabeel.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring as ComposeSpring

/**
 * Sabeel motion tokens — the single source of truth for animation timing.
 *
 * RULE: never hand-write a `spring(...)` or `tween(...)` with raw numbers in a
 * screen or component — reference a token here instead. This is what keeps two
 * unrelated screens moving on the same "clock": if the counter's spring ever
 * gets re-tuned, every place that borrowed [Spring.Counter] moves with it
 * instead of silently drifting apart.
 *
 * Naming mirrors how the tokens are used, not where: [Spring.Counter] is used
 * by both the arc sweep and the odometer digits because they're meant to feel
 * like one physical object, not two coincidentally-similar ones.
 *
 * NOTE on [Spring.CardExpand]: it's a function, not a val, and that's
 * deliberate. `expandVertically`/`shrinkVertically` need
 * `FiniteAnimationSpec<IntSize>` while `fadeIn`/`fadeOut`/`animateFloatAsState`
 * need `FiniteAnimationSpec<Float>` — a single fixed-type val can only serve
 * one of those. Making it a generic function lets each call site infer its
 * own type from context, the same way the original hand-written
 * `spring(stiffness = ...)` call sites did, while still keeping the actual
 * damping/stiffness numbers in exactly one place.
 */
object SabeelMotion {

    /** M3 "Emphasized Decelerate" — for content arriving on screen (sheets, pushes). */
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    /** M3 "Emphasized Accelerate" — for content leaving the screen. */
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    object Spring {
        /**
         * The counter's shared clock: odometer digit roll + arc sweep.
         *
         * POLISH-01 tuning (Part 3): changed from `dampingRatio=0.62f, stiffness=620f`
         * to `DampingRatioNoBouncy (1.0), stiffness=500f`.
         *
         * Rationale: the old 0.62 damping produced a ~6° overshoot on the arc before
         * settling — visually a flicker past the target angle. For a *data-driven*
         * progress indicator (not a bouncy button) there is no physical justification
         * for overshoot; it reads as imprecision. NoBouncy gives critically-damped
         * settlement (mathematically the fastest path to rest without oscillation).
         * Stiffness reduced to 500f (from 620f) to keep the settle duration ~identical
         * (~140ms on Pixel 6) so rhythm is unchanged while overshoot is gone.
         */
        val Counter: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = 500f
        )

        /**
         * Generic slide variant of [Counter] — same physics, but infers [T] from context.
         * Use for `slideInVertically`/`slideOutVertically` in [OdometerCounter] and
         * any other offset-typed animation that needs to share the counter clock.
         * The identical damping/stiffness ensures arc sweep + digit roll feel like
         * one physical object even though they animate different value types.
         */
        fun <T> CounterSlide(): FiniteAnimationSpec<T> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = 500f
        )

        /**
         * Press-down feedback — scale dip on finger contact.
         *
         * Kept as `DampingRatioNoBouncy + StiffnessHigh` for the dip phase (critically
         * damped, instantaneous feel). The spring-back uses [PressRelease] to achieve
         * the "releasing a bead" micro-bounce.
         */
        val PressFeedback: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = ComposeSpring.StiffnessHigh
        )

        /**
         * Press-release spring — scale returns to 1.0 after finger lifts.
         *
         * SMOOTH-07: explicit token for the spring-back so the dip (PressFeedback)
         * and release use clearly distinct tokens and the difference is traceable.
         * 0.45 damping gives one gentle rebound — the "bead released" feel.
         */
        val PressRelease: FiniteAnimationSpec<Float> = spring(
            dampingRatio = 0.45f,
            stiffness    = 500f
        )

        /**
         * Completion overlay — scale from 0.88→1.0 when dhikr target is reached.
         *
         * POLISH-02 tuning (Part 3): changed from DampingRatioMediumBouncy (0.5) to
         * DampingRatioLowBouncy (0.75) + StiffnessMedium.
         *
         * Rationale: the previous 0.5 damping overshot to ~1.08× before settling,
         * injecting a playful/celebratory bounce into what should be a meditative,
         * earned arrival. LowBouncy (0.75) is the M3 "Emphasized" settle — present,
         * confident, no frivolous rebound.
         */
        val Completion: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioLowBouncy,
            stiffness    = ComposeSpring.StiffnessMedium
        )

        /**
         * Card / row expand-collapse (DhikrCard, Home sections, Wird target editor).
         * Generic on purpose — see the class-level NOTE above. Call as
         * `SabeelMotion.Spring.CardExpand()`, type is inferred from context.
         */
        fun <T> CardExpand(): FiniteAnimationSpec<T> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = ComposeSpring.StiffnessMediumLow
        )

        /**
         * CTA card press scale — used by [AccentCard].
         *
         * iOS-parity rationale: Apple's UIKit press feedback is critically damped.
         * Any damping < 1.0 produces an overshoot on release that reads as "cheap
         * Android bounce". NoBouncy (1.0) gives a clean, confident settle —
         * the card snaps in and out of the dip without drama.
         *
         * StiffnessMedium (400f) keeps the dip and release fast enough to feel
         * instant but not so stiff that it looks like a jump-cut.
         */
        val CardPress: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = ComposeSpring.StiffnessMedium
        )

        /** Chevron / disclosure-indicator rotation — same feel everywhere a chevron appears. */
        val ChevronRotate: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness    = ComposeSpring.StiffnessMediumLow
        )
    }

    object Duration {
        /** Colour cross-fades (border tint, background tint, container swaps). */
        const val ColorTransition = 300

        /** Sheet-route enter (SabeelNavHost). */
        const val SheetEnter = 420

        /** Sheet-route exit — faster than enter, matches CompletionRest's "don't block" exit. */
        const val SheetExit = 260

        /** Home hero-card crossfade when session state changes underneath the user. */
        const val HeroCardCrossfadeIn = 250
        const val HeroCardCrossfadeOut = 150

        /**
         * Ring pulse on tap — grows to full circle radius.
         * SMOOTH-02: extracted from TasbihCircle raw 600ms so the value is trackable.
         */
        const val RingExpand = 600

        /** Ring alpha fade — slightly shorter than expand so the ring vanishes while still growing. */
        const val RingFade = 550

        /**
         * Tab-level enter: content arriving on screen after a tab switch.
         * 300ms matches M3 "Standard" tab transition duration. Longer than exit
         * so the incoming screen gets full attention as it arrives.
         */
        const val TabEnter = 300

        /**
         * Tab-level exit: content leaving screen during a tab switch.
         * 200ms — fast enough to not feel like it's blocking the incoming screen.
         */
        const val TabExit = 200
    }

    object Tween {
        val ColorTransition = tween<androidx.compose.ui.graphics.Color>(Duration.ColorTransition)
    }

    /**
     * Apple-parity tab transition factory functions.
     *
     * iOS UIKit tab switches combine:
     *   1. An opacity transition (fade) using a deceleration curve on enter,
     *      acceleration curve on exit.
     *   2. A micro-scale shift (0.96→1.0 enter, 1.0→0.98 exit) so content
     *      physically "arrives" or "departs" — not just blinks in/out.
     *
     * Each is a function (not a val) because [EnterTransition] and
     * [ExitTransition] are sealed classes, not primitives — storing them as
     * vals would capture references that could become stale if the animation
     * system changes. Functions ensure fresh instances at every call site.
     */
    fun tabEnter(): androidx.compose.animation.EnterTransition =
        androidx.compose.animation.scaleIn(
            initialScale  = 0.96f,
            animationSpec = tween(Duration.TabEnter, easing = EmphasizedDecelerate)
        ) + androidx.compose.animation.fadeIn(
            animationSpec = tween(Duration.TabEnter, easing = EmphasizedDecelerate)
        )

    fun tabExit(): androidx.compose.animation.ExitTransition =
        androidx.compose.animation.scaleOut(
            targetScale   = 0.98f,
            animationSpec = tween(Duration.TabExit, easing = EmphasizedAccelerate)
        ) + androidx.compose.animation.fadeOut(
            animationSpec = tween(Duration.TabExit, easing = EmphasizedAccelerate)
        )
}
