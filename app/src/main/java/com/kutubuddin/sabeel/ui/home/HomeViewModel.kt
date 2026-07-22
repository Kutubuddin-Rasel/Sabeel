package com.kutubuddin.sabeel.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineDispatcher
import com.kutubuddin.sabeel.di.DefaultDispatcher
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.kutubuddin.sabeel.ui.i18n.localizeDigits
import javax.inject.Inject

/**
 * DIP + ISP: depends on [TasbihCounterObserver] and [StreakObserver] — the two
 * narrow slices this screen actually reads — never the full read/write
 * [com.kutubuddin.sabeel.domain.repository.TasbihRepository]. Home never
 * mutates the counter, so it has no business depending on a contract that
 * could.
 *
 * THREAD-04 fix: `today` was previously a compile-time snapshot (`LocalDate.now()` at
 * ViewModel construction). If the user kept the app open past midnight, all session
 * queries would silently return yesterday's data. It is now a [MutableStateFlow]
 * updated by a [BroadcastReceiver] for [Intent.ACTION_DATE_CHANGED], which fires
 * exactly at midnight. The receiver is registered in [onCleared] to prevent leaks.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
    private val counterObserver: TasbihCounterObserver,
    private val streakObserver: StreakObserver,
    private val observeWirdProgress: ObserveWirdProgress,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    // THREAD-04: reactive date — updates exactly at midnight via system broadcast.
    // Using MutableStateFlow so flatMapLatest re-subscribes session queries on date change.
    @RequiresApi(Build.VERSION_CODES.O)
    private val _today = MutableStateFlow(LocalDate.now().toString())

    // BroadcastReceiver registered in init, unregistered in onCleared — no leak window.
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_DATE_CHANGED) {
                _today.value = LocalDate.now().toString()
            }
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(
                dateChangeReceiver,
                IntentFilter(Intent.ACTION_DATE_CHANGED)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val sessionGroup = _today.flatMapLatest { today ->
        combine(
            sessionRepository.getAggregatedSessionsForDate(today).map { list -> list.map { it.toSummary() } },
            sessionRepository.getTotalCountForDate(today),
            // OPT-04: distinctUntilChanged — allTime only changes after a debounced flush,
            // not on every tap. Filtering duplicate emissions avoids rebuilding HomeState
            // for a value that hasn't changed since the last counter increment.
            sessionRepository.getTotalAllTime().distinctUntilChanged(),
            sessionRepository.getTotalSessionCount(),
            settingsRepository.language
        ) { sessions, totalToday, totalAllTime, totalSessions, language ->
            listOf<Any>(sessions, totalToday, totalAllTime, totalSessions, language)
        }
    }

    // JANK-04: date label as a reactive flow derived from _today + language.
    // _today is already refreshed at midnight by the BroadcastReceiver.
    // HomeScreen reads state.todayLabel — never stale, never a remember(language) hack.
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateLabelFlow = combine(
        _today,
        settingsRepository.language
    ) { _, lang ->
        val locale = when (lang) {
            "ar" -> Locale("ar")
            "tr" -> Locale("tr")
            else -> Locale.ENGLISH
        }
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", locale)
        ).localizeDigits(lang)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val counterGroup = _today.flatMapLatest { today ->
        combine(
            observeWirdProgress(today),
            // OPT-04: We rely entirely on the DB for totals. This prevents double-counting and 
            // flickers because the DB is guaranteed to be accurate by the time the Home tab is visible.
            counterObserver.activeCount,
            counterObserver.activeDhikr,
            streakObserver.streak
        ) { wird, lastCount, lastDhikr, streak ->
            listOf<Any?>(wird, lastCount, lastDhikr, streak)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val state: StateFlow<HomeState> = combine(
        sessionGroup,
        counterGroup,
        dateLabelFlow
    ) { s, c, todayLabel ->
        @Suppress("UNCHECKED_CAST")
        val sessions      = s[0] as List<SessionSummary>
        val totalToday    = s[1] as Int
        val totalAllTime  = s[2] as Int
        val totalSessions = s[3] as Int
        val language      = s[4] as String

        val wirdProgress = c[0] as com.kutubuddin.sabeel.domain.model.WirdProgress
        val wird = wirdProgress.toSummary()
        val lastCount   = c[1] as Int
        val lastDhikr   = c[2] as ActiveDhikr
        val streak      = c[3] as? Streak

        val greeting = resolveGreeting()
        val target = lastDhikr.target

        val nextGoalItem = wirdProgress.items.firstOrNull { !it.isComplete }
        val heroState = when {
            nextGoalItem != null -> {
                HomeHeroState.Resume(
                    dhikrKey = nextGoalItem.dhikrKey,
                    displayName = nextGoalItem.displayName,
                    lastCount = nextGoalItem.countToday,
                    target = nextGoalItem.target,
                    isDailyGoal = true
                )
            }
            lastCount > 0 -> {
                HomeHeroState.Resume(
                    dhikrKey = lastDhikr.key,
                    displayName = lastDhikr.displayName,
                    lastCount = lastCount,
                    target = target,
                    isDailyGoal = false
                )
            }
            wirdProgress.items.isEmpty() -> HomeHeroState.SetupGoal
            else -> HomeHeroState.None
        }

        HomeState(
            todaysSessions    = sessions.toImmutableList(),
            totalToday        = displayedToday(totalToday),
            wird              = wird,
            currentStreak     = displayedStreak(streak?.count ?: 0, lastCount),
            totalAllTime      = displayedAllTime(totalAllTime),
            totalSessionCount = totalSessions,
            heroState         = heroState,
            greeting          = greeting,
            language          = language,
            todayLabel        = todayLabel
        )
    }.flowOn(defaultDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    override fun onCleared() {
        super.onCleared()
        // THREAD-04: unregister receiver to prevent context leak after ViewModel is destroyed.
        try {
            context.unregisterReceiver(dateChangeReceiver)
        } catch (_: IllegalArgumentException) {
            // Receiver was never registered (e.g. API < O) — safe to ignore.
        }
    }

    companion object {
        internal fun displayedToday(savedToday: Int): Int = savedToday

        internal fun displayedAllTime(savedAllTime: Int): Int = savedAllTime

        internal fun displayedStreak(streakCount: Int, activeCount: Int): Int =
            if (streakCount == 0 && activeCount > 0) 1 else streakCount
    }

    private fun resolveGreeting(): GreetingType = greetingTypeForHour(LocalTime.now().hour)

    private fun com.kutubuddin.sabeel.data.local.db.dao.AggregatedSessionRow.toSummary(): SessionSummary = SessionSummary(
        dhikrKey    = dhikrKey,
        displayName = DhikrCatalog.displayNameFor(dhikrKey),
        count       = totalCount,
        isComplete  = false // We can omit this or compute if needed.
    )
}