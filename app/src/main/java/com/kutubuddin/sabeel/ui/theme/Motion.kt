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
         * The counter's shared clock: odometer digit roll + arc sweep + ring pulse.
         * Weighted and calm on purpose — this is the most-repeated motion in the
         * app, so it must never feel twitchy.
         */
        val Counter: FiniteAnimationSpec<Float> = spring(
            dampingRatio = 0.62f,
            stiffness = 620f
        )

        /**
         * Card / row expand-collapse (DhikrCard, Home sections, Wird target editor).
         * Generic on purpose — see the class-level NOTE above. Call as
         * `SabeelMotion.Spring.CardExpand()`, type is inferred from context.
         */
        fun <T> CardExpand(): FiniteAnimationSpec<T> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness = ComposeSpring.StiffnessMediumLow
        )

        /** Chevron / disclosure-indicator rotation — same feel everywhere a chevron appears. */
        val ChevronRotate: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioNoBouncy,
            stiffness = ComposeSpring.StiffnessMediumLow
        )

        /** Press feedback (scale-down on tap) for cards and buttons. */
        val PressFeedback: FiniteAnimationSpec<Float> = spring(
            dampingRatio = ComposeSpring.DampingRatioMediumBouncy,
            stiffness = ComposeSpring.StiffnessMedium
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
    }

    object Tween {
        val ColorTransition = tween<androidx.compose.ui.graphics.Color>(Duration.ColorTransition)
    }
}
