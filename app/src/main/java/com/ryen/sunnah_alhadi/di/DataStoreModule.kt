package com.ryen.sunnah_alhadi.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.ryen.sunnah_alhadi.data.datastore.dataStore
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<ProtoUserPreferences> = context.dataStore
}
