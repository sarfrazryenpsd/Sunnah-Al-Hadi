package com.ryen.sunnah_alhadi.di

import com.ryen.sunnah_alhadi.domain.useCase.sotd.CheckSotdAvailabilityUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdNotificationScheduledUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldScheduleSotdNotificationUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import org.koin.dsl.module

val sotdModule = module {
    // SOTD Use Cases
    factory { GetCurrentSotdUseCase(get(), get()) }
    factory { GetCurrentSotdFlowUseCase(get(), get()) }
    factory { GenerateNewSotdUseCase(get(), get()) }
    factory { MarkSotdAsSeenUseCase(get()) }
    factory { CheckSotdAvailabilityUseCase(get()) }
    factory { ShouldShowSotdCardUseCase(get()) }
    factory { ShouldScheduleSotdNotificationUseCase(get()) }
    factory { MarkSotdNotificationScheduledUseCase(get()) }
}