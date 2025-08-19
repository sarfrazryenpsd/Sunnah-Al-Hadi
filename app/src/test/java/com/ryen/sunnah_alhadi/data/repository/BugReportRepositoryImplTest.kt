@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ryen.sunnah_alhadi.data.repository

import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ryen.sunnah_alhadi.MainDispatcherRule
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BugReportEntity
import com.ryen.sunnah_alhadi.data.model.toEntity
import com.ryen.sunnah_alhadi.domain.model.BugReport
import com.ryen.sunnah_alhadi.domain.model.BugReportException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class BugReportRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks
    @MockK(relaxed = true)
    private lateinit var bugReportDao: BugReportDao

    @MockK(relaxed = true)
    private lateinit var crashlytics: FirebaseCrashlytics

    private lateinit var repository: BugReportRepositoryImpl

    // Test data
    private val testReport = BugReport(
        id = "test-id-123",
        description = "Test bug description",
        userEmail = "test@example.com",
        appVersion = "1.0.0",
        deviceInfo = "Test Device",
        timestamp = 1234567890L,
        isSynced = false
    )

    private val testReportEntity = BugReportEntity(
        id = "test-id-123",
        description = "Test bug description",
        userEmail = "test@example.com",
        appVersion = "1.0.0",
        deviceInfo = "Test Device",
        timestamp = 1234567890L,
        isSynced = false
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = BugReportRepositoryImpl(bugReportDao, crashlytics)
    }

    @Test
    fun `saveBugReport should save locally and send to crashlytics successfully`() = runTest {
        // Given
        coEvery { bugReportDao.insertBugReport(any()) } returns Unit
        coEvery { bugReportDao.markAsSynced(any()) } returns Unit

        // When
        repository.saveBugReport(testReport)

        // Then
        coVerify(exactly = 1) {
            bugReportDao.insertBugReport(
                match {
                    it.id == testReportEntity.id &&
                            it.description == testReportEntity.description &&
                            it.userEmail == testReportEntity.userEmail &&
                            it.appVersion == testReportEntity.appVersion &&
                            it.deviceInfo == testReportEntity.deviceInfo &&
                            it.timestamp == testReportEntity.timestamp &&
                            !it.isSynced // because it's false before syncing
                }
            )
        }

        coVerify(exactly = 1) { bugReportDao.markAsSynced("test-id-123") }

        // Verify Crashlytics interactions
        verify { crashlytics.setCustomKey("bug_report_id", "test-id-123") }
        verify { crashlytics.setCustomKey("user_email", "test@example.com") }
        verify { crashlytics.setCustomKey("app_version", "1.0.0") }
        verify { crashlytics.setCustomKey("device_info", "Test Device") }
        verify { crashlytics.setCustomKey("report_timestamp", 1234567890L) }
        verify { crashlytics.setCustomKey("is_user_reported_bug", true) }
        verify { crashlytics.recordException(any<BugReportException>()) }
        verify { crashlytics.log("=== USER BUG REPORT ===") }
        verify { crashlytics.log("Report ID: test-id-123") }
        verify { crashlytics.log("User Email: test@example.com") }
        verify { crashlytics.log("Description: Test bug description") }
        verify { crashlytics.log("Device: Test Device") }
        verify { crashlytics.log("App Version: 1.0.0") }
        verify { crashlytics.log("========================") }
    }


    @Test
    fun `saveBugReport should throw exception when dao insertion fails`() = runTest {
        // Given
        val expectedException = Exception("Database error")
        coEvery { bugReportDao.insertBugReport(any()) } throws expectedException

        try {
            repository.saveBugReport(testReport)
            fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            assertEquals("Failed to save bug report: Database error", e.message)
        }

        coVerify(exactly = 1) { bugReportDao.insertBugReport(any()) }
        coVerify(exactly = 0) { bugReportDao.markAsSynced(any()) }
    }

    @Test
    fun `getPendingReports should return mapped domain objects`() = runTest {
        // Given
        val entities = listOf(testReportEntity, testReportEntity.copy(id = "test-id-456"))
        coEvery { bugReportDao.getPendingReports() } returns entities

        // When
        val result = repository.getPendingReports()

        // Then
        assertEquals(2, result.size)
        assertEquals("test-id-123", result[0].id)
        assertEquals("test-id-456", result[1].id)
        coVerify(exactly = 1) { bugReportDao.getPendingReports() }
    }

    @Test
    fun `markReportAsSynced should call dao markAsSynced`() = runTest {
        // Given
        coEvery { bugReportDao.markAsSynced(any()) } returns Unit

        // When
        repository.markReportAsSynced("test-id")

        // Then
        coVerify(exactly = 1) { bugReportDao.markAsSynced("test-id") }
    }

    @Test
    fun `syncPendingReports should sync all pending reports successfully`() = runTest {
        // Given
        val pendingReports = listOf(
            testReport,
            testReport.copy(id = "test-id-456", description = "Another bug")
        )
        coEvery { bugReportDao.getPendingReports() } returns pendingReports.map { it.toEntity() }
        coEvery { bugReportDao.markAsSynced(any()) } returns Unit
        coEvery { bugReportDao.deleteSyncedOldReports(any()) } returns Unit

        // When
        repository.syncPendingReports()

        // Then
        coVerify(exactly = 1) { bugReportDao.getPendingReports() }
        coVerify(exactly = 2) { bugReportDao.markAsSynced(any()) }
        coVerify(exactly = 1) { bugReportDao.deleteSyncedOldReports(any()) }

        // Verify Crashlytics calls for both reports
        verify(exactly = 2) { crashlytics.recordException(any<BugReportException>()) }
    }

    @Test
    fun `syncPendingReports should handle individual report failures gracefully`() = runTest {
        // Given
        val pendingReports = listOf(testReport, testReport.copy(id = "test-id-456"))
        coEvery { bugReportDao.getPendingReports() } returns pendingReports.map { it.toEntity() }
        coEvery { bugReportDao.markAsSynced("test-id-123") } returns Unit
        coEvery { bugReportDao.markAsSynced("test-id-456") } throws Exception("Sync failed")
        coEvery { bugReportDao.deleteSyncedOldReports(any()) } returns Unit

        // When
        repository.syncPendingReports()

        // Then - should not throw exception and continue processing
        coVerify(exactly = 1) { bugReportDao.getPendingReports() }
        coVerify(exactly = 2) { bugReportDao.markAsSynced(any()) }
        coVerify(exactly = 1) { bugReportDao.deleteSyncedOldReports(any()) }
    }

    @Test
    fun `syncPendingReports should record exception when sync process fails`() = runTest {
        // Given
        val expectedException = Exception("Database connection failed")
        coEvery { bugReportDao.getPendingReports() } throws expectedException

        // When
        repository.syncPendingReports()

        // Then - should record exception to Crashlytics
        verify { crashlytics.recordException(match<Exception> {
            it.message?.contains("Bug report sync failed: Database connection failed") == true
        }) }
    }

    @Test
    fun `getPendingReportsCount should return count from dao`() = runTest {
        // Given
        coEvery { bugReportDao.getPendingReportsCount() } returns 5

        // When
        val result = repository.getPendingReportsCount()

        // Then
        assertEquals(5, result)
        coVerify(exactly = 1) { bugReportDao.getPendingReportsCount() }
    }
}