package com.ryen.sunnah_alhadi.domain.useCase.bugReport

import com.ryen.sunnah_alhadi.domain.model.BugReport
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import javax.inject.Inject

class SubmitBugReportUseCase @Inject constructor(
    private val repository: BugReportRepository
) {
    suspend operator fun invoke(
        description: String,
        userEmail: String,
        appVersion: String,
        deviceInfo: String
    ): Result<String> {
        return try {
            if (description.isBlank()) {
                return Result.failure(Exception("Bug description cannot be empty"))
            }

            val report = BugReport(
                description = description.trim(),
                userEmail = userEmail.trim(),
                appVersion = appVersion,
                deviceInfo = deviceInfo,
                timestamp = System.currentTimeMillis()
            )

            repository.saveBugReport(report)
            Result.success(report.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}