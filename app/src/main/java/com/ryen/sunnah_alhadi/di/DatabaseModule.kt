package com.ryen.sunnah_alhadi.di

import android.content.Context
import androidx.room.Room
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sunnah_database.db"
        )
            .createFromAsset("database/sunnah_database.db")
            .build()
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideSunnahDao(db: AppDatabase): SunnahDao = db.sunnahDao()

}

