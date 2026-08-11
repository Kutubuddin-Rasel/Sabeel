package com.kitalonlabs.sabeel.domain.repository

/**
 * The counting screen's complete contract — composed, never duplicated, from
 * five ISP-scoped interfaces. [com.kitalonlabs.sabeel.ui.tasbih.TasbihViewModel]
 * is the one consumer that legitimately needs the full surface (it drives
 * counting, Smart Flow, Pocket Mode, and streak display for a single screen).
 * Every other consumer depends on exactly the narrower interface it uses:
 *  - [com.kitalonlabs.sabeel.ui.home.HomeViewModel] → [TasbihCounterObserver] + [StreakObserver]
 *  - [com.kitalonlabs.sabeel.domain.usecase.ObserveWirdProgress] → [TasbihCounterObserver]
 *
 * [com.kitalonlabs.sabeel.data.repository.TasbihRepositoryImpl] is the single
 * concrete implementation and is bound to each interface token in
 * [com.kitalonlabs.sabeel.di.RepositoryModule] — one Singleton instance behind
 * six narrow contracts.
 */
interface TasbihRepository :
    TasbihCounterObserver,
    TasbihCounterMutator,
    StreakObserver,
    SmartFlowSettings