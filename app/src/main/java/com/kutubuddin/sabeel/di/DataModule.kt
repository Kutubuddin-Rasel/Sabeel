package com.kutubuddin.sabeel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.kutubuddin.sabeel.data.local.db.SabeelDatabase
import com.kutubuddin.sabeel.data.local.db.dao.CustomDhikrDao
import com.kutubuddin.sabeel.data.local.db.dao.DhikrSessionDao
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
import com.kutubuddin.sabeel.data.local.db.dao.WirdDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("sabeel_preferences") }
        )

    @Provides
    @Singleton
    fun provideSabeelDatabase(@ApplicationContext context: Context): SabeelDatabase =
        Room.databaseBuilder(
            context,
            SabeelDatabase::class.java,
            "sabeel-db"
        )
        .addMigrations(SabeelDatabase.MIGRATION_1_2, SabeelDatabase.MIGRATION_2_3)
        .build()

    @Provides
    @Singleton
    fun provideSakinahDao(database: SabeelDatabase): SakinahDao = database.sakinahDao()

    @Provides
    @Singleton
    fun provideDhikrSessionDao(database: SabeelDatabase): DhikrSessionDao =
        database.dhikrSessionDao()

    @Provides
    @Singleton
    fun provideCustomDhikrDao(database: SabeelDatabase): CustomDhikrDao =
        database.customDhikrDao()

    @Provides
    @Singleton
    fun provideWirdDao(database: SabeelDatabase): WirdDao = database.wirdDao()
}
