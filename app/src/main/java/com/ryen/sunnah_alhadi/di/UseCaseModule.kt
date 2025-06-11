package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.domain.useCase.ExportSunnahAsImageUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetAllSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetBookmarkedSunnahsFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahDetailUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahOfTheDayUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetTopicWithSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ScheduleDailyReminderUseCase
import com.ryen.sunnah_alhadi.domain.useCase.SearchSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import org.koin.dsl.module


val useCaseModule = module {

    // Home
    factory { GetHomeDataUseCase(get(), get()) }

    // Topic Detail
    factory { GetTopicWithSunnahsUseCase(get(), get()) }

    // Sunnah Detail
    factory { GetSunnahDetailUseCase(get(), get()) }
    factory { ToggleBookmarkUseCase(get()) }

    // Browse All
    factory { SearchSunnahsUseCase(get()) }
    factory { GetAllSunnahsUseCase(get()) }

    // Saved
    factory { GetBookmarkedSunnahsFlowUseCase(get()) }

    // User Preferences
    factory { UpdateUserPreferencesUseCase(get()) }
    factory { GetUserPreferencesFlowUseCase(get()) }

    // Daily Reminder
    factory { GetSunnahOfTheDayUseCase(get(), get()) }
    factory { ScheduleDailyReminderUseCase() }

    // Export
    factory { ExportSunnahAsImageUseCase() }

    // Disclaimer
    /*factory { HandleDisclaimerUseCase(get()) }
    factory { ShouldShowDisclaimerUseCase(get()) }*/
}