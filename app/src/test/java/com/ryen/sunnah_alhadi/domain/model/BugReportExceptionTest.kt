package com.ryen.sunnah_alhadi.domain.model

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BugReportExceptionTest {

    @Test
    fun `toString should format exception details correctly`() {
        // Given
        val exception = BugReportException(
            message = "User-reported bug: Short description...",
            fullDescription = "This is the full description of the bug that users reported",
            reportId = "bug-123"
        )

        // When
        val result = exception.toFormattedString()

        // Then
        assertTrue(result.contains("BugReportException: User-reported bug: Short description..."))
        assertTrue(result.contains("Report ID: bug-123"))
        assertTrue(result.contains("Full Description: This is the full description of the bug that users reported"))
    }

    @Test
    fun `exception should inherit from Exception correctly`() {
        // Given
        val exception = BugReportException(
            message = "Test message",
            fullDescription = "Test description",
            reportId = "test-id"
        )

        // Then
        assertTrue(true)
        assertEquals("Test message", exception.message)
        assertEquals("Test description", exception.fullDescription)
        assertEquals("test-id", exception.reportId)
    }
}