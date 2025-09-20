package com.ryen.sunnah_alhadi.di

import androidx.datastore.core.DataStore
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.repository.BookmarkRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.BugReportRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.CategoryRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.ImageExportRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.SunnahRepositoryImpl
import com.ryen.sunnah_alhadi.data.repository.UserPreferencesRepositoryImpl
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.ImageExportRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSunnahRepository(
        sunnahDao: SunnahDao,
        ioDispatcher: CoroutineDispatcher
    ): SunnahRepository {
        return SunnahRepositoryImpl(sunnahDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        ioDispatcher: CoroutineDispatcher
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao,
        ioDispatcher: CoroutineDispatcher
    ): BookmarkRepository {
        return BookmarkRepositoryImpl(bookmarkDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        dataStore: DataStore<ProtoUserPreferences>,
        @ApplicationScope applicationScope: kotlinx.coroutines.CoroutineScope
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore, applicationScope)
    }

    @Provides
    @Singleton
    fun provideBugReportRepository(
        bugReportDao: BugReportDao,
        crashlytics: FirebaseCrashlytics
    ): BugReportRepository {
        return BugReportRepositoryImpl(bugReportDao, crashlytics)
    }

    @Provides
    @Singleton
    fun provideImageExportRepository(
        exportSunnahAsImageUseCase: ExportSunnahAsImageUseCase
    ): ImageExportRepository {
        return ImageExportRepositoryImpl(exportSunnahAsImageUseCase)
    }
}
