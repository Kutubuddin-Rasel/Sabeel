package com.kutubuddin.sabeel.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity
import com.kutubuddin.sabeel.domain.model.ActiveDhikr
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.Streak
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.StreakObserver
import com.kutubuddin.sabeel.domain.repository.TasbihCounterObserver
import com.kutubuddin.sabeel.domain.usecase.MarkWirdGoalHintSeen
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdGoalHintVisibility
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * DIP + ISP: depends on [TasbihCounterObserver] and [StreakObserver] — the two
 * narrow slices this screen actually reads — never the full read/write
 * [com.kutubuddin.sabeel.domain.repository.TasbihRepository]. Home never
 * mutates the counter, so it has no business depending on a contract that
 * could.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val counterObserver: TasbihCounterObserver,
    private val streakObserver: StreakObserver,
    private val observeWirdProgress: ObserveWirdProgress,
    private val observeWirdGoalHintVisibility: ObserveWirdGoalHintVisibility,
    private val markWirdGoalHintSeen: MarkWirdGoalHintSeen
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now().toString()

    private val sessionGroup = combine(
        sessionRepository.getSessionsForDate(today).map { list -> list.map { it.toSummary() } },
        sessionRepository.getTotalCountForDate(today),
        sessionRepository.getTotalAllTime(),
        sessionRepository.getTotalSessionCount(),
        settingsRepository.language
    ) { sessions, totalToday, totalAllTime, totalSessions, language ->
        listOf<Any>(sessions, totalToday, totalAllTime, totalSessions, language)
    }

    private val counterGroup = combine(
        observeWirdProgress(today).map { it.toSummary() },
        counterObserver.activeCount,
        counterObserver.activeDhikr,
        streakObserver.streak,
        settingsRepository.showStreaks
    ) { wird, lastCount, lastDhikr, streak, showStreaks ->
        listOf<Any?>(wird, lastCount, lastDhikr, streak, showStreaks)
    }

    val state: StateFlow<HomeState> = combine(
        sessionGroup,
        counterGroup,
        observeWirdGoalHintVisibility()
    ) { s, c, wirdGoalHint ->
        @Suppress("UNCHECKED_CAST")
        val sessions      = s[0] as List<SessionSummary>
        val totalToday    = s[1] as Int
        val totalAllTime  = s[2] as Int
        val totalSessions = s[3] as Int
        val language      = s[4] as String

        val wird        = c[0] as WirdSummary
        val lastCount   = c[1] as Int
        val lastDhikr   = c[2] as ActiveDhikr
        val streak      = c[3] as? Streak
        val showStreaks = c[4] as Boolean

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
            todaysSessions    = sessions,
            totalToday        = displayedToday(totalToday, lastCount, target),
            wird              = wird,
            currentStreak     = displayedStreak(streak?.count ?: 0, lastCount),
            totalAllTime      = displayedAllTime(totalAllTime, lastCount, target),
            totalSessionCount = totalSessions,
            resumeSession     = resume,
            greeting          = greeting,
            showStreaks       = showStreaks,
            language          = language,
            wirdGoalHint      = wirdGoalHint
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    companion object {
        internal fun displayedToday(savedToday: Int, activeCount: Int, target: Int): Int =
            savedToday + if (activeCount in 1 until target) activeCount else 0

        internal fun displayedAllTime(savedAllTime: Int, activeCount: Int, target: Int): Int =
            savedAllTime + if (activeCount in 1 until target) activeCount else 0

        internal fun displayedStreak(streakCount: Int, activeCount: Int): Int =
            if (streakCount == 0 && activeCount > 0) 1 else streakCount
    }

    /** Fired by every wird-edit entry point; idempotent, safe to call repeatedly. */
    fun onWirdEditEntryUsed() = viewModelScope.launch { markWirdGoalHintSeen() }

    private fun resolveGreeting(): GreetingType = greetingTypeForHour(LocalTime.now().hour)

    private fun DhikrSessionEntity.toSummary(): SessionSummary = SessionSummary(
        dhikrKey    = dhikrKey,
        displayName = DhikrCatalog.displayNameFor(dhikrKey),
        count       = count,
        isComplete  = isComplete
    )
}