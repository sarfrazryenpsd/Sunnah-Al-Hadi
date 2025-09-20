package com.ryen.sunnah_alhadi.di

import android.content.Context
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetAllCategoriesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetAllSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetBookmarkedSunnahsFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetTopicWithSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.bugReport.GetPendingBugReportsCountUseCase
import com.ryen.sunnah_alhadi.domain.useCase.bugReport.SubmitBugReportUseCase
import com.ryen.sunnah_alhadi.domain.useCase.bugReport.SyncBugReportsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides fun provideGetHomeDataUseCase(
        repository: CategoryRepository,
        prefs: UserPreferencesRepository
    ) = GetHomeDataUseCase(repository, prefs)

    @Provides fun provideGetTopicWithSunnahsUseCase(
        categoryRepository: CategoryRepository,
        sunnahRepository: SunnahRepository
    ) = GetTopicWithSunnahsUseCase(categoryRepository, sunnahRepository)

    @Provides fun provideToggleBookmarkUseCase(
        repository: BookmarkRepository
    ) = ToggleBookmarkUseCase(repository)



    @Provides fun provideGetAllSunnahsUseCase(
        sunnahRepository: SunnahRepository
    ) = GetAllSunnahsUseCase(sunnahRepository)

    @Provides fun provideGetBookmarkedSunnahsFlowUseCase(
        repository: BookmarkRepository
    ) = GetBookmarkedSunnahsFlowUseCase(repository)

    @Provides fun provideGetSunnahCountsUseCase(
        sunnahRepository: SunnahRepository
    ) = GetSunnahCountsUseCase(sunnahRepository)

    @Provides fun provideUpdateUserPreferencesUseCase(
        repository: UserPreferencesRepository
    ) = UpdateUserPreferencesUseCase(repository)

    @Provides fun provideGetUserPreferencesFlowUseCase(
        repository: UserPreferencesRepository
    ) = GetUserPreferencesFlowUseCase(repository)

    @Provides fun provideGetUserPreferencesUseCase(
        repository: UserPreferencesRepository
    ) = GetUserPreferencesUseCase(repository)

    @Provides
    fun provideExportSunnahAsImageUseCase(
        @ApplicationContext context: Context
    ) = ExportSunnahAsImageUseCase(context)

    @Provides
    fun provideGetAllCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetAllCategoriesUseCase(categoryRepository)

    @Provides
    fun provideGetSunnahByIdUseCase(
        sunnahRepository: SunnahRepository,
    ) = GetSunnahByIdUseCase(sunnahRepository)

    @Provides
    fun provideSubmitBugReportUseCase(
        repository: BugReportRepository
    ): SubmitBugReportUseCase {
        return SubmitBugReportUseCase(repository)
    }

    @Provides
    fun provideSyncBugReportsUseCase(
        repository: BugReportRepository
    ): SyncBugReportsUseCase {
        return SyncBugReportsUseCase(repository)
    }

    @Provides
    fun provideGetPendingBugReportsCountUseCase(
        repository: BugReportRepository
    ): GetPendingBugReportsCountUseCase {
        return GetPendingBugReportsCountUseCase(repository)
    }
}
