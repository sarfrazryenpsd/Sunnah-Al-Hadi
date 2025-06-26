package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetAllSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetBookmarkedSunnahsFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahOfTheDayUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetTopicWithSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ScheduleDailyReminderUseCase
import com.ryen.sunnah_alhadi.domain.useCase.SearchSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetHomeDataUseCase(
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

    @Provides fun provideSearchSunnahsUseCase(
        sunnahRepository: SunnahRepository
    ) = SearchSunnahsUseCase(sunnahRepository)

    @Provides fun provideGetAllSunnahsUseCase(
        sunnahRepository: SunnahRepository
    ) = GetAllSunnahsUseCase(sunnahRepository)

    @Provides fun provideGetBookmarkedSunnahsFlowUseCase(
        repository: BookmarkRepository
    ) = GetBookmarkedSunnahsFlowUseCase(repository)

    @Provides fun provideUpdateUserPreferencesUseCase(
        repository: UserPreferencesRepository
    ) = UpdateUserPreferencesUseCase(repository)

    @Provides fun provideGetUserPreferencesFlowUseCase(
        repository: UserPreferencesRepository
    ) = GetUserPreferencesFlowUseCase(repository)

    @Provides fun provideGetSunnahOfTheDayUseCase(
        sunnahRepository: SunnahRepository,
        userPreferencesRepository: UserPreferencesRepository
    ) = GetSunnahOfTheDayUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideScheduleDailyReminderUseCase() = ScheduleDailyReminderUseCase()

    @Provides
    fun provideExportSunnahAsImageUseCase() = ExportSunnahAsImageUseCase()

    @Provides
    fun provideGetSunnahByIdUseCase(
        sunnahRepository: SunnahRepository,
    ) = GetSunnahByIdUseCase(sunnahRepository)
}
