package com.kutubuddin.sabeel.di

import com.kutubuddin.sabeel.data.repository.DhikrRepositoryImpl
import com.kutubuddin.sabeel.data.repository.SessionRepositoryImpl
import com.kutubuddin.sabeel.data.repository.SettingsRepositoryImpl
import com.kutubuddin.sabeel.data.repository.TasbihRepositoryImpl
import com.kutubuddin.sabeel.data.repository.WirdGoalDiscoveryRepositoryImpl
import com.kutubuddin.sabeel.data.repository.WirdRepositoryImpl
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.domain.repository.SmartFlowSettings
import com.kutubuddin.sabeel.domain.repository.StreakObserver
import com.kutubuddin.sabeel.domain.repository.TasbihCounterMutator
import com.kutubuddin.sabeel.domain.repository.TasbihCounterObserver
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import com.kutubuddin.sabeel.domain.repository.WirdGoalDiscoveryRepository
import com.kutubuddin.sabeel.domain.repository.WirdRepository
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