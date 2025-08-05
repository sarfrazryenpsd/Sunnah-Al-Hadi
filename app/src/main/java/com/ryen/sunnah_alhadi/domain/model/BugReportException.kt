package com.ryen.sunnah_alhadi.domain.model

class BugReportException(
    message: String,
    val fullDescription: String,
    val reportId: String
) : Exception(message) {

    fun toFormattedString(): String {
        return buildString {
            appendLine("BugReportException: $message")
            appendLine("Report ID: $reportId")
            appendLine("Full Description: $fullDescription")
        }
    }
}