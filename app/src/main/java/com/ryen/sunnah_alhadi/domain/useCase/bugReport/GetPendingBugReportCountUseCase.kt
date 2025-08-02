package com.ryen.sunnah_alhadi.domain.useCase.bugReport

import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import javax.inject.Inject

class GetPendingBugReportsCountUseCase @Inject constructor(
    private val repository: BugReportRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getPendingReportsCount()
    }
}