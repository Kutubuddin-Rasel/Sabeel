package com.kutubuddin.sabeel.ui.home

/**
 * Time-of-day greeting, decoupled from display language.
 *
 * The ViewModel resolves the *type* from the clock (pure, testable); the screen
 * resolves the type to localized copy via `LocalStrings`. This keeps the
 * ViewModel free of presentation language (SRP/DIP).
 */
enum class GreetingType { FAJR, MORNING, DHUHR, AFTERNOON, ASR, MAGHRIB, ISHA, DEFAULT }

/** Maps a 24-hour clock hour to its greeting type. */
fun greetingTypeForHour(hour: Int): GreetingType = when (hour) {
    in 4..6   -> GreetingType.FAJR
    in 7..11  -> GreetingType.MORNING
    in 12..13 -> GreetingType.DHUHR
    in 14..15 -> GreetingType.AFTERNOON
    in 16..17 -> GreetingType.ASR
    in 18..19 -> GreetingType.MAGHRIB
    in 20..21 -> GreetingType.ISHA
    else      -> GreetingType.DEFAULT
}
