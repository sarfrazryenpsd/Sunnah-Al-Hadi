package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sotdModule = module {

    // Notification components
    single { SotdNotificationHelper(androidContext()) }
    single { SotdNotificationScheduler(androidContext()) }

    // Use cases
    single { GenerateNewSotdUseCase(get(), get()) }
    single { GetCurrentSotdUseCase(get(), get()) }
    single { ShouldShowSotdCardUseCase(get()) }
    single { MarkSotdAsSeenUseCase(get()) }
}