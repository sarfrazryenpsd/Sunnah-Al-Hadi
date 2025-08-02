@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ryen.sunnah_alhadi.domain.useCase.bugReport

import com.ryen.sunnah_alhadi.MainDispatcherRule
import com.ryen.sunnah_alhadi.domain.model.BugReport
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SubmitBugReportUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var repository: BugReportRepository

    private lateinit var useCase: SubmitBugReportUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        useCase = SubmitBugReportUseCase(repository)
    }

    @Test
    fun `invoke should return success when bug report is valid`() = runTest {
        // Given
        coEvery { repository.saveBugReport(any()) } returns Unit

        // When
        val result = useCase(
            description = "Valid bug description",
            userEmail = "test@example.com",
            appVersion = "1.0.0",
            deviceInfo = "Test Device"
        )

        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        coVerify(exactly = 1) { repository.saveBugReport(any()) }
    }

    @Test
    fun `invoke should return failure when description is blank`() = runTest {
        // When
        val result = useCase(
            description = "   ",
            userEmail = "test@example.com",
            appVersion = "1.0.0",
            deviceInfo = "Test Device"
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("Bug description cannot be empty", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.saveBugReport(any()) }
    }

    @Test
    fun `invoke should return failure when description is empty`() = runTest {
        // When
        val result = useCase(
            description = "",
            userEmail = "test@example.com",
            appVersion = "1.0.0",
            deviceInfo = "Test Device"
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals("Bug description cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should trim input values`() = runTest {
        // Given
        val slot = slot<BugReport>()
        coEvery { repository.saveBugReport(capture(slot)) } returns Unit

        // When
        useCase(
            description = "  Valid description  ",
            userEmail = "  test@example.com  ",
            appVersion = "1.0.0",
            deviceInfo = "Test Device"
        )

        // Then
        assertEquals("Valid description", slot.captured.description)
        assertEquals("test@example.com", slot.captured.userEmail)
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val exception = Exception("Repository error")
        coEvery { repository.saveBugReport(any()) } throws exception

        // When
        val result = useCase(
            description = "Valid description",
            userEmail = "test@example.com",
            appVersion = "1.0.0",
            deviceInfo = "Test Device"
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should generate unique report IDs`() = runTest {
        // Given
        val capturedReports = mutableListOf<BugReport>()
        coEvery { repository.saveBugReport(capture(capturedReports)) } returns Unit

        // When
        useCase("Description 1", "test1@example.com", "1.0.0", "Device 1")
        useCase("Description 2", "test2@example.com", "1.0.0", "Device 2")

        // Then
        assertEquals(2, capturedReports.size)
        assertNotEquals(capturedReports[0].id, capturedReports[1].id)
    }
}