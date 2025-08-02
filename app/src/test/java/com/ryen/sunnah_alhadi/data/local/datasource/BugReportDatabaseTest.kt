package com.ryen.sunnah_alhadi.data.local.datasource

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BugReportEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class BugReportDatabaseTest {

    private lateinit var database: BugReportDatabase
    private lateinit var bugReportDao: BugReportDao

    private val testReportEntity = BugReportEntity(
        id = "test-id-123",
        description = "Test bug description",
        userEmail = "test@example.com",
        appVersion = "1.0.0",
        deviceInfo = "Test Device",
        timestamp = System.currentTimeMillis(),
        isSynced = false
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BugReportDatabase::class.java
        ).allowMainThreadQueries().build()

        bugReportDao = database.bugReportDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertBugReport should save report successfully`() = runTest {
        // When
        bugReportDao.insertBugReport(testReportEntity)

        // Then
        val allReports = bugReportDao.getAllReports()
        assertEquals(1, allReports.size)
        assertEquals(testReportEntity.id, allReports[0].id)
        assertEquals(testReportEntity.description, allReports[0].description)
    }

    @Test
    fun `insertBugReport should replace on conflict`() = runTest {
        // Given
        bugReportDao.insertBugReport(testReportEntity)
        val updatedEntity = testReportEntity.copy(description = "Updated description")

        // When
        bugReportDao.insertBugReport(updatedEntity)

        // Then
        val allReports = bugReportDao.getAllReports()
        assertEquals(1, allReports.size)
        assertEquals("Updated description", allReports[0].description)
    }

    @Test
    fun `getPendingReports should return only unsynced reports`() = runTest {
        // Given
        val syncedReport = testReportEntity.copy(id = "synced", isSynced = true)
        val pendingReport = testReportEntity.copy(id = "pending", isSynced = false)

        bugReportDao.insertBugReport(syncedReport)
        bugReportDao.insertBugReport(pendingReport)

        // When
        val pendingReports = bugReportDao.getPendingReports()

        // Then
        assertEquals(1, pendingReports.size)
        assertEquals("pending", pendingReports[0].id)
        assertFalse(pendingReports[0].isSynced)
    }

    @Test
    fun `getPendingReports should return reports ordered by timestamp desc`() = runTest {
        // Given
        val older = testReportEntity.copy(id = "older", timestamp = 1000L)
        val newer = testReportEntity.copy(id = "newer", timestamp = 2000L)

        bugReportDao.insertBugReport(older)
        bugReportDao.insertBugReport(newer)

        // When
        val reports = bugReportDao.getPendingReports()

        // Then
        assertEquals(2, reports.size)
        assertEquals("newer", reports[0].id) // Should be first (newer timestamp)
        assertEquals("older", reports[1].id)
    }

    @Test
    fun `markAsSynced should update report sync status`() = runTest {
        // Given
        bugReportDao.insertBugReport(testReportEntity)

        // When
        bugReportDao.markAsSynced("test-id-123")

        // Then
        val allReports = bugReportDao.getAllReports()
        assertEquals(1, allReports.size)
        assertTrue(allReports[0].isSynced)

        val pendingReports = bugReportDao.getPendingReports()
        assertEquals(0, pendingReports.size)
    }

    @Test
    fun `deleteSyncedOldReports should delete only old synced reports`() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30 * 24 * 60 * 60 * 1000L)
        val fortyDaysAgo = now - (40 * 24 * 60 * 60 * 1000L)

        val oldSynced = testReportEntity.copy(
            id = "old-synced",
            isSynced = true,
            createdAt = fortyDaysAgo
        )
        val recentSynced = testReportEntity.copy(
            id = "recent-synced",
            isSynced = true,
            createdAt = now
        )
        val oldPending = testReportEntity.copy(
            id = "old-pending",
            isSynced = false,
            createdAt = fortyDaysAgo
        )

        bugReportDao.insertBugReport(oldSynced)
        bugReportDao.insertBugReport(recentSynced)
        bugReportDao.insertBugReport(oldPending)

        // When
        bugReportDao.deleteSyncedOldReports(thirtyDaysAgo)

        // Then
        val remainingReports = bugReportDao.getAllReports()
        assertEquals(2, remainingReports.size)

        val remainingIds = remainingReports.map { it.id }
        assertFalse(remainingIds.contains("old-synced")) // Should be deleted
        assertTrue(remainingIds.contains("recent-synced")) // Should remain
        assertTrue(remainingIds.contains("old-pending")) // Should remain (not synced)
    }

    @Test
    fun `getPendingReportsCount should return correct count`() = runTest {
        // Given
        val synced = testReportEntity.copy(id = "synced", isSynced = true)
        val pending1 = testReportEntity.copy(id = "pending1", isSynced = false)
        val pending2 = testReportEntity.copy(id = "pending2", isSynced = false)

        bugReportDao.insertBugReport(synced)
        bugReportDao.insertBugReport(pending1)
        bugReportDao.insertBugReport(pending2)

        // When
        val count = bugReportDao.getPendingReportsCount()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `getAllReports should return all reports ordered by timestamp desc`() = runTest {
        // Given
        val report1 = testReportEntity.copy(id = "1", timestamp = 1000L)
        val report2 = testReportEntity.copy(id = "2", timestamp = 3000L)
        val report3 = testReportEntity.copy(id = "3", timestamp = 2000L)

        bugReportDao.insertBugReport(report1)
        bugReportDao.insertBugReport(report2)
        bugReportDao.insertBugReport(report3)

        // When
        val allReports = bugReportDao.getAllReports()

        // Then
        assertEquals(3, allReports.size)
        assertEquals("2", allReports[0].id) // timestamp: 3000L
        assertEquals("3", allReports[1].id) // timestamp: 2000L
        assertEquals("1", allReports[2].id) // timestamp: 1000L
    }
}