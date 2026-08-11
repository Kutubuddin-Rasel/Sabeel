package com.kitalonlabs.sabeel.di

import com.kitalonlabs.sabeel.data.notifications.NotificationSchedulerImpl
import com.kitalonlabs.sabeel.data.notifications.NotificationServiceImpl
import com.kitalonlabs.sabeel.domain.notifications.NotificationScheduler
import com.kitalonlabs.sabeel.domain.notifications.NotificationService
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
