package com.kitalonlabs.sabeel.ui.home

/**
 * Time-of-day greeting, decoupled from display language.
 *
 * The ViewModel resolves the *type* from the clock (pure, testable); the screen
 * resolves the type to localized copy via `LocalStrings`. This keeps the
 * ViewModel free of presentation language (SRP/DIP).
 *
 * IX-06b: despite the enum's names, these are fixed clock-hour buckets, not
 * real salah times — actual Fajr/Maghrib/etc. move with date and location
 * (Dhaka's Maghrib alone ranges ~17:05–18:50 across the year) and this file
 * has no location or date input to account for that. The strings shown for
 * each bucket (see UiText.greetingFajr etc.) were reworded to describe the
 * hour-of-day rather than assert a specific prayer is currently due, so the
 * mismatch is cosmetic rather than a false religious claim. Real per-location
 * accuracy would need device location + a proper calculation method — a
 * separate feature, not a fix to this enum.
 */
enum class GreetingType { FAJR, MORNING, DHUHR, AFTERNOON, ASR, MAGHRIB, ISHA, DEFAULT }

/** Maps a 24-hour clock hour to its greeting type — see the class doc above. */
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
