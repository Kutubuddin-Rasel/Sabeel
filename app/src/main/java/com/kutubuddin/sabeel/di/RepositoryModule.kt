package com.kutubuddin.sabeel.di

import com.kutubuddin.sabeel.data.repository.TasbihRepositoryImpl
import com.kutubuddin.sabeel.domain.repository.TasbihRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTasbihRepository(
        impl: TasbihRepositoryImpl
    ): TasbihRepository
}
