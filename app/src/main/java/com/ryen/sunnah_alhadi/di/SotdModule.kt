package com.ryen.sunnah_alhadi.di

import android.content.Context
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SotdModule {

    @Provides
    fun provideSotdNotificationHelper(@ApplicationContext context: Context): SotdNotificationHelper {
        return SotdNotificationHelper(context)
    }

    @Provides
    fun provideSotdNotificationScheduler(@ApplicationContext context: Context): SotdNotificationScheduler {
        return SotdNotificationScheduler(context)
    }

    @Provides
    fun provideGenerateNewSotdUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        sunnahRepository: SunnahRepository
    ): GenerateNewSotdIdUseCase = GenerateNewSotdIdUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideGetCurrentSotdUseCase(
        userPreferencesRepository: UserPreferencesRepository,
        sunnahRepository: SunnahRepository
    ): GetCurrentSotdUseCase = GetCurrentSotdUseCase(sunnahRepository, userPreferencesRepository)

    @Provides
    fun provideShouldShowSotdCardUseCase(
        repository: UserPreferencesRepository
    ): ShouldShowSotdCardUseCase = ShouldShowSotdCardUseCase(repository)

    @Provides
    fun provideMarkSotdAsSeenUseCase(
        repository: UserPreferencesRepository
    ): MarkSotdAsSeenUseCase = MarkSotdAsSeenUseCase(repository)
}
