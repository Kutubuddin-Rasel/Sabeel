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
            sessionRepository.getSessionsForDate(today).map { list -> list.map { it.toSummary() } },
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
            observeWirdProgress(today).map { it.toSummary() },
            // OPT-04: debounce(300ms) — activeCount is updated on every tap (atomic in-memory).
            // Without debounce the entire 9-flow combine + HomeState rebuild runs 33 times
            // per dhikr target completion. 300ms is imperceptible lag on the Home tab;
            // the Tasbih screen itself reads the atomic _activeCount directly and is instant.
            counterObserver.activeCount.debounce(300L),
            counterObserver.sessionStartCount.debounce(300L),
            counterObserver.activeDhikr,
            streakObserver.streak
        ) { wird, lastCount, startCount, lastDhikr, streak ->
            listOf<Any?>(wird, lastCount, startCount, lastDhikr, streak)
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

        val wird        = c[0] as WirdSummary
        val lastCount   = c[1] as Int
        val startCount  = c[2] as Int
        val lastDhikr   = c[3] as ActiveDhikr
        val streak      = c[4] as? Streak

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
            todaysSessions    = sessions.toImmutableList(),
            totalToday        = displayedToday(totalToday, lastCount, startCount),
            wird              = wird,
            currentStreak     = displayedStreak(streak?.count ?: 0, lastCount),
            totalAllTime      = displayedAllTime(totalAllTime, lastCount, startCount),
            totalSessionCount = totalSessions,
            resumeSession     = resume,
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
        internal fun displayedToday(savedToday: Int, activeCount: Int, startCount: Int): Int =
            savedToday + if (activeCount > startCount) (activeCount - startCount) else 0

        internal fun displayedAllTime(savedAllTime: Int, activeCount: Int, startCount: Int): Int =
            savedAllTime + if (activeCount > startCount) (activeCount - startCount) else 0

        internal fun displayedStreak(streakCount: Int, activeCount: Int): Int =
            if (streakCount == 0 && activeCount > 0) 1 else streakCount
    }

    private fun resolveGreeting(): GreetingType = greetingTypeForHour(LocalTime.now().hour)

    private fun DhikrSessionEntity.toSummary(): SessionSummary = SessionSummary(
        id          = id,
        dhikrKey    = dhikrKey,
        displayName = DhikrCatalog.displayNameFor(dhikrKey),
        count       = count,
        isComplete  = isComplete
    )
}