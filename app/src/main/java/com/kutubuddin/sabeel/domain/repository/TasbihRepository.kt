package com.kutubuddin.sabeel.domain.repository

/**
 * The counting screen's complete contract — composed, never duplicated, from
 * five ISP-scoped interfaces. [com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel]
 * is the one consumer that legitimately needs the full surface (it drives
 * counting, Smart Flow, Pocket Mode, and streak display for a single screen).
 * Every other consumer depends on exactly the narrower interface it uses:
 *  - [com.kutubuddin.sabeel.ui.home.HomeViewModel] → [TasbihCounterObserver] + [StreakObserver]
 *  - [com.kutubuddin.sabeel.domain.usecase.ObserveWirdProgress] → [TasbihCounterObserver]
 *  - [com.kutubuddin.sabeel.service.PocketModeService] → [TasbihCounterMutator]
 *
 * [com.kutubuddin.sabeel.data.repository.TasbihRepositoryImpl] is the single
 * concrete implementation and is bound to each interface token in
 * [com.kutubuddin.sabeel.di.RepositoryModule] — one Singleton instance behind
 * six narrow contracts.
 */
interface TasbihRepository :
    TasbihCounterObserver,
    TasbihCounterMutator,
    StreakObserver,
    SmartFlowSettings,
    PocketModeSettings