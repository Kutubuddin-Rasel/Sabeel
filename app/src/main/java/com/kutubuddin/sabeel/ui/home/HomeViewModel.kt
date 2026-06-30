package com.kutubuddin.sabeel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val tasbihRepository: TasbihRepository
) : ViewModel() {

    private val today = LocalDate.now().toString()

    // Split into two typed combines to avoid intersection-type inference issues
    private val sessionGroup = combine(
        sessionRepository.getSessionsForDate(today),
        sessionRepository.getTotalCountForDate(today),
        sessionRepository.getTotalAllTime(),
        sessionRepository.getTotalSessionCount()
    ) { sessions, totalToday, totalAllTime, totalSessions ->
        listOf<Any>(sessions, totalToday, totalAllTime, totalSessions)
    }

    private val counterGroup = combine(
        settingsRepository.dailyGoal,
        tasbihRepository.activeCount,
        tasbihRepository.activeDhikr,
        tasbihRepository.streak
    ) { goal, lastCount, lastDhikr, streak ->
        listOf<Any?>(goal, lastCount, lastDhikr, streak)
    }

    val state: StateFlow<HomeState> = combine(sessionGroup, counterGroup) { s, c ->
        @Suppress("UNCHECKED_CAST")
        val sessions     = s[0] as List<DhikrSessionEntity>
        val totalToday   = s[1] as Int
        val totalAllTime = s[2] as Int
        val totalSessions= s[3] as Int

        val dailyGoal    = c[0] as Int
        val lastCount    = c[1] as Int
        val lastDhikr    = c[2] as DhikrType
        val streak       = c[3] as? Streak

        val (greeting, icon) = resolveGreeting()

        val resume = if (lastCount > 0) {
            ResumeSession(
                dhikrType = lastDhikr,
                lastCount = lastCount,
                target = lastDhikr.defaultTarget
            )
        } else null

        HomeState(
            todaysSessions = sessions,
            totalToday     = totalToday,
            dailyGoal      = dailyGoal,
            currentStreak  = streak?.count ?: 0,
            longestStreak  = streak?.longestStreak ?: 0,
            totalAllTime   = totalAllTime,
            totalSessionCount = totalSessions,
            resumeSession  = resume,
            greeting       = greeting,
            greetingIcon   = icon
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    private fun resolveGreeting(): Pair<String, String> {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 4..6   -> "Fajr time — a blessed start" to "☀️"
            in 7..11  -> "Good morning" to "🌤️"
            in 12..13 -> "Dhuhr time" to "🌤️"
            in 14..15 -> "Good afternoon" to "☀️"
            in 16..17 -> "Asr time" to "🌤️"
            in 18..19 -> "Maghrib time" to "🌅"
            in 20..21 -> "Isha time" to "🌙"
            else      -> "Assalamu alaikum" to "🌙"
        }
    }
}
