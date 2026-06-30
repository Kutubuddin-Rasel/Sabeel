package com.kutubuddin.sabeel.di

import com.kutubuddin.sabeel.data.hardware.HapticEngineImpl
import com.kutubuddin.sabeel.data.hardware.PowerSaverManagerImpl
import com.kutubuddin.sabeel.data.hardware.SystemSabeelVibrator
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.haptic.SabeelVibrator
import com.kutubuddin.sabeel.domain.power.PowerSaverManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindSabeelVibrator(
        impl: SystemSabeelVibrator
    ): SabeelVibrator

    @Binds
    @Singleton
    abstract fun bindHapticEngine(
        impl: HapticEngineImpl
    ): HapticEngine

    @Binds
    @Singleton
    abstract fun bindPowerSaverManager(
        impl: PowerSaverManagerImpl
    ): PowerSaverManager
}
