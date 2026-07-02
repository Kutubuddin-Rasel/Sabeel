package com.kutubuddin.sabeel.domain.model

/**
 * The countable runtime identity of the dhikr on the Count screen.
 *
 * Deliberately decoupled from the 6-value [DhikrType] enum: the counter used to
 * hard-wire to `DhikrType`, so any catalog entry outside those six (e.g.
 * "SubhanAllahi l-Azim", the morning/evening adhkar) fell back to SubhanAllah.
 * By resolving a plain String [key] from [DhikrCatalog] into this value object,
 * *every* catalog entry — built-in or custom — becomes countable with its own
 * Arabic, name and target.
 *
 * Persistence stays String-keyed (no Room migration): [key] is the stored id.
 */
data class ActiveDhikr(
    val key: String,
    val arabicText: String,
    val displayName: String,
    val target: Int,
    val spiritualReward: LocalizedText,
    val hadithRef: String,
    /** Non-null when this is a multi-step sequence (Tasbīḥ after Salah). */
    val sequenceKey: String? = null
)
