package com.ryen.sunnah_alhadi.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.BugReportDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

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
    @Singleton
    fun provideBugReportDatabase(@ApplicationContext context: Context): BugReportDatabase {
        return Room.databaseBuilder(
            context,
            BugReportDatabase::class.java,
            "bug_reports_database" // Separate database
        )
            .build()
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideSunnahDao(db: AppDatabase): SunnahDao = db.sunnahDao()

    @Provides
    fun provideBookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()

    @Provides
    fun provideBugReportDao(database: BugReportDatabase): BugReportDao {
        return database.bugReportDao()
    }



    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}


