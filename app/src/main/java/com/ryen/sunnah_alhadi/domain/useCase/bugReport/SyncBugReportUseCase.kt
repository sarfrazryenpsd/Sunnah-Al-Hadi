package com.ryen.sunnah_alhadi.domain.useCase.bugReport

import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import javax.inject.Inject

class SyncBugReportsUseCase @Inject constructor(
    private val repository: BugReportRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            val pendingCount = repository.getPendingReportsCount()
            repository.syncPendingReports()
            Result.success(pendingCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}