package com.kutubuddin.sabeel.di

import com.kutubuddin.sabeel.data.notifications.NotificationSchedulerImpl
import com.kutubuddin.sabeel.data.notifications.NotificationServiceImpl
import com.kutubuddin.sabeel.domain.notifications.NotificationScheduler
import com.kutubuddin.sabeel.domain.notifications.NotificationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun bindNotificationService(
        impl: NotificationServiceImpl
    ): NotificationService

    @Binds
    abstract fun bindNotificationScheduler(
        impl: NotificationSchedulerImpl
    ): NotificationScheduler
}
