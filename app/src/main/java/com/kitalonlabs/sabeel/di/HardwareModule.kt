package com.kitalonlabs.sabeel.di

import com.kitalonlabs.sabeel.data.hardware.HapticEngineImpl
import com.kitalonlabs.sabeel.data.hardware.PowerSaverManagerImpl
import com.kitalonlabs.sabeel.data.hardware.SystemSabeelVibrator
import com.kitalonlabs.sabeel.domain.haptic.HapticEngine
import com.kitalonlabs.sabeel.domain.haptic.SabeelVibrator
import com.kitalonlabs.sabeel.domain.power.PowerSaverManager
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
