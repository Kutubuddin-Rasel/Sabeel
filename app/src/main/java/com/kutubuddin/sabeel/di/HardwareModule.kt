package com.kutubuddin.sabeel.di

import com.kutubuddin.sabeel.data.hardware.AndroidTasbihHapticController
import com.kutubuddin.sabeel.domain.haptic.TasbihHapticController
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
    abstract fun bindTasbihHapticController(
        impl: AndroidTasbihHapticController
    ): TasbihHapticController
}
