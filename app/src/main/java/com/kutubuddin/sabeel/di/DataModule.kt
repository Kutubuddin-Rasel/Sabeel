package com.kutubuddin.sabeel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.kutubuddin.sabeel.data.local.db.SabeelDatabase
import com.kutubuddin.sabeel.data.local.db.dao.SakinahDao
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
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("sabeel_preferences") }
        )
    }

    @Provides
    @Singleton
    fun provideSabeelDatabase(@ApplicationContext context: Context): SabeelDatabase {
        return Room.databaseBuilder(
            context,
            SabeelDatabase::class.java,
            "sabeel-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSakinahDao(database: SabeelDatabase): SakinahDao {
        return database.sakinahDao()
    }
}
