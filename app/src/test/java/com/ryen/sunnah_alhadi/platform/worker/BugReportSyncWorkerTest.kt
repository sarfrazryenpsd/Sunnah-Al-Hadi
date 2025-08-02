@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ryen.sunnah_alhadi.platform.worker

import android.content.Context
import android.os.Build
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ryen.sunnah_alhadi.MainDispatcherRule
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class BugReportSyncWorkerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var workerParams: WorkerParameters

    @MockK(relaxed = true)
    private lateinit var bugReportRepository: BugReportRepository

    @MockK(relaxed = true)
    private lateinit var crashlytics: FirebaseCrashlytics

    private lateinit var worker: BugReportSyncWorker

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        worker = BugReportSyncWorker(context, workerParams, crashlytics, bugReportRepository)
    }

    @Test
    fun `doWork should return success when no pending reports`() = runTest {
        coEvery { bugReportRepository.getPendingReportsCount() } returns 0

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        verify { crashlytics.log("Starting background bug report sync") }
        verify { crashlytics.log("No pending bug reports to sync") }
        coVerify { bugReportRepository.getPendingReportsCount() }
        coVerify(exactly = 0) { bugReportRepository.syncPendingReports() }
    }

    @Test
    fun `doWork should sync pending reports successfully`() = runTest {
        coEvery { bugReportRepository.getPendingReportsCount() } returns 3
        coEvery { bugReportRepository.syncPendingReports() } returns Unit

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        verify { crashlytics.log("Starting background bug report sync") }
        verify { crashlytics.log("Background bug report sync completed. Synced 3 reports") }
        coVerify { bugReportRepository.getPendingReportsCount() }
        coVerify { bugReportRepository.syncPendingReports() }
    }

    @Test
    fun `doWork should retry on IOException`() = runTest {
        val ioException = IOException("Network error")
        coEvery { bugReportRepository.getPendingReportsCount() } returns 2
        coEvery { bugReportRepository.syncPendingReports() } throws ioException

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        verify {
            crashlytics.recordException(match<Exception> {
                it.message?.contains("Background bug report sync failed: Network error") == true
            })
        }
    }

    @Test
    fun `doWork should retry on network-related exception`() = runTest {
        val networkException = Exception("Network timeout occurred")
        coEvery { bugReportRepository.getPendingReportsCount() } returns 1
        coEvery { bugReportRepository.syncPendingReports() } throws networkException

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        verify {
            crashlytics.recordException(match<Exception> {
                it.message?.contains("Background bug report sync failed: Network timeout occurred") == true
            })
        }
    }

    @Test
    fun `doWork should return failure on non-network exceptions`() = runTest {
        val databaseException = Exception("Database corruption")
        coEvery { bugReportRepository.getPendingReportsCount() } returns 1
        coEvery { bugReportRepository.syncPendingReports() } throws databaseException

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
        verify {
            crashlytics.recordException(match<Exception> {
                it.message?.contains("Background bug report sync failed: Database corruption") == true
            })
        }
    }

    @Test
    fun `doWork should handle SecurityException gracefully`() = runTest {
        val securityException = SecurityException("Permission denied")
        coEvery { bugReportRepository.getPendingReportsCount() } throws securityException

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result) // updated line

        verify {
            crashlytics.recordException(match<Exception> {
                it.message?.contains("Background bug report sync failed: Permission denied") == true
            })
        }
    }
}
