package com.ryen.sunnah_alhadi.domain.useCase

class ScheduleDailyReminderUseCase(
    // Add WorkManager dependency here
) : UseCase<Boolean, Unit>() {

    override suspend fun execute(parameters: Boolean) {
        if (parameters) {
            // Schedule daily reminder
            // WorkManager implementation
        } else {
            // Cancel daily reminder
            // WorkManager cancellation
        }
    }
}