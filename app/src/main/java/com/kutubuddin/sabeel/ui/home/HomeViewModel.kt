package com.kutubuddin.sabeel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val tasbihRepository: TasbihRepository,
    private val observeWirdProgress: ObserveWirdProgress
) : ViewModel() {

    private val today = LocalDate.now().toString()

    // Split into two typed combines to avoid intersection-type inference issues
    private val sessionGroup = combine(
        sessionRepository.getSessionsForDate(today),
        sessionRepository.getTotalCountForDate(today),
        sessionRepository.getTotalAllTime(),
        sessionRepository.getTotalSessionCount(),
        settingsRepository.language
    ) { sessions, totalToday, totalAllTime, totalSessions, language ->
        listOf<Any>(sessions, totalToday, totalAllTime, totalSessions, language)
    }

    private val counterGroup = combine(
        observeWirdProgress(today).map { it.toSummary() },
        tasbihRepository.activeCount,
        tasbihRepository.activeDhikr,
        tasbihRepository.streak,
        settingsRepository.showStreaks
    ) { wird, lastCount, lastDhikr, streak, showStreaks ->
        listOf<Any?>(wird, lastCount, lastDhikr, streak, showStreaks)
    }

    val state: StateFlow<HomeState> = combine(sessionGroup, counterGroup) { s, c ->
        @Suppress("UNCHECKED_CAST")
        val sessions     = s[0] as List<DhikrSessionEntity>
        val totalToday   = s[1] as Int
        val totalAllTime = s[2] as Int
        val totalSessions= s[3] as Int
        val language     = s[4] as String

        val wird         = c[0] as WirdSummary
        val lastCount    = c[1] as Int
        val lastDhikr    = c[2] as ActiveDhikr
        val streak       = c[3] as? Streak
        val showStreaks  = c[4] as Boolean

        val greeting = resolveGreeting()

        val target = lastDhikr.target

        val resume = if (lastCount > 0) {
            ResumeSession(
                dhikrKey = lastDhikr.key,
                displayName = lastDhikr.displayName,
                lastCount = lastCount,
                target = target
            )
        } else null

        HomeState(
            todaysSessions = sessions,
            totalToday     = displayedToday(totalToday, lastCount, target),
            wird           = wird,
            currentStreak  = displayedStreak(streak?.count ?: 0, lastCount),
            totalAllTime   = displayedAllTime(totalAllTime, lastCount, target),
            totalSessionCount = totalSessions,
            resumeSession  = resume,
            greeting       = greeting,
            showStreaks    = showStreaks,
            language       = language
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    companion object {
        /**
         * Today's total = saved (completed) sessions + the current in-progress
         * round. The in-progress count is added ONLY while the round is unfinished
         * (`activeCount < target`); once it completes a session is saved, so adding
         * the still-live count would double-count (a finished 33 would read 66).
         */
        internal fun displayedToday(savedToday: Int, activeCount: Int, target: Int): Int =
            savedToday + if (activeCount in 1 until target) activeCount else 0

        /** Same guard as [displayedToday], applied to the all-time total. */
        internal fun displayedAllTime(savedAllTime: Int, activeCount: Int, target: Int): Int =
            savedAllTime + if (activeCount in 1 until target) activeCount else 0

        /**
         * Counting today (even an unfinished round) registers consistency. The
         * persisted streak only advances on completion, so promote a zero streak
         * to 1 while a session is actively in progress.
         */
        internal fun displayedStreak(streakCount: Int, activeCount: Int): Int =
            if (streakCount == 0 && activeCount > 0) 1 else streakCount
    }

    /** Pure time→type mapping; the screen localizes the copy. */
    private fun resolveGreeting(): GreetingType = greetingTypeForHour(LocalTime.now().hour)
}
