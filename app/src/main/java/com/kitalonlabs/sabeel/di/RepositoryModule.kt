package com.kitalonlabs.sabeel.di

import com.kitalonlabs.sabeel.data.repository.DhikrRepositoryImpl
import com.kitalonlabs.sabeel.data.repository.SessionRepositoryImpl
import com.kitalonlabs.sabeel.data.repository.SettingsRepositoryImpl
import com.kitalonlabs.sabeel.data.repository.TasbihRepositoryImpl
import com.kitalonlabs.sabeel.data.repository.WirdGoalDiscoveryRepositoryImpl
import com.kitalonlabs.sabeel.data.repository.WirdRepositoryImpl
import com.kitalonlabs.sabeel.domain.repository.DhikrRepository
import com.kitalonlabs.sabeel.domain.repository.SessionRepository
import com.kitalonlabs.sabeel.domain.repository.SettingsRepository
import com.kitalonlabs.sabeel.domain.repository.SmartFlowSettings
import com.kitalonlabs.sabeel.domain.repository.StreakObserver
import com.kitalonlabs.sabeel.domain.repository.TasbihCounterMutator
import com.kitalonlabs.sabeel.domain.repository.TasbihCounterObserver
import com.kitalonlabs.sabeel.domain.repository.TasbihRepository
import com.kitalonlabs.sabeel.domain.repository.WirdGoalDiscoveryRepository
import com.kitalonlabs.sabeel.domain.repository.WirdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindTasbihRepository(impl: TasbihRepositoryImpl): TasbihRepository

    // ISP: the same Singleton impl, bound to each narrower slice so DIP consumers
    // (HomeViewModel, ObserveWirdProgress) can inject exactly
    // what they use instead of the full TasbihRepository surface.
    @Binds @Singleton
    abstract fun bindTasbihCounterObserver(impl: TasbihRepositoryImpl): TasbihCounterObserver

    @Binds @Singleton
    abstract fun bindTasbihCounterMutator(impl: TasbihRepositoryImpl): TasbihCounterMutator

    @Binds @Singleton
    abstract fun bindStreakObserver(impl: TasbihRepositoryImpl): StreakObserver

    @Binds @Singleton
    abstract fun bindSmartFlowSettings(impl: TasbihRepositoryImpl): SmartFlowSettings


    @Binds @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds @Singleton
    abstract fun bindDhikrRepository(impl: DhikrRepositoryImpl): DhikrRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindWirdRepository(impl: WirdRepositoryImpl): WirdRepository

    @Binds @Singleton
    abstract fun bindWirdGoalDiscoveryRepository(
        impl: WirdGoalDiscoveryRepositoryImpl
    ): WirdGoalDiscoveryRepository
}