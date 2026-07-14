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
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * OPT-02: Settings DataStore — stores user preferences (theme, haptics, language, etc).
     * Written only when user explicitly changes a setting — typically 0-1 writes per session.
     * Isolated from counter writes so that tap events never trigger settings flow re-evaluation.
     */
    @Named("settings")
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("sabeel_settings") }
        )

    /**
     * OPT-02: Counter DataStore — stores live counter state (count, dhikr key, step index, etc).
     * Written on every debounced tap flush. Separated from settings so high-frequency counter
     * writes do NOT trigger re-evaluation of the 13 settings flow operators.
     */
    @Named("counter")
    @Provides
    @Singleton
    fun provideCounterDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("sabeel_counter") }
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
        // OPT-06: safe guard — prevents crash if user somehow downgrades past migration chain.
        // dropAllTables=true: on a version downgrade, the database is fully rebuilt rather than
        // crashing with an IllegalStateException. Acceptable for a dev-phase app.
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
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
