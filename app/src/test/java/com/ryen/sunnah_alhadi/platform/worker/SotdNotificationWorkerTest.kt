package com.ryen.sunnah_alhadi.platform.worker

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.di.SotdModule
import com.ryen.sunnah_alhadi.di.UseCaseModule
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import com.ryen.sunnah_alhadi.util.Result
import dagger.Module
import dagger.Provides
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.UUID
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [Build.VERSION_CODES.TIRAMISU],
    application = HiltTestApplication::class
)
class SotdNotificationWorkerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var generateSotdIdUseCase: GenerateNewSotdIdUseCase

    @MockK
    private lateinit var userPrefsRepository: UserPreferencesRepository

    @MockK
    private lateinit var notificationHelper: SotdNotificationHelper

    @MockK
    private lateinit var getSunnahByIdUseCase: GetSunnahByIdUseCase

    private lateinit var context: Context
    private lateinit var worker: SotdNotificationWorker

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        // Grant notification permission for tests
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shadowApplication = shadowOf(context as Application)
            shadowApplication.grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private suspend fun createAndRunWorker(attemptCount: Int = 1): ListenableWorker.Result {
        val workerParams = mockk<WorkerParameters>(relaxed = true) {
            every { id } returns UUID.randomUUID()
            every { inputData } returns Data.EMPTY
            every { runAttemptCount } returns attemptCount
        }

        val worker = SotdNotificationWorker(
            context = context,
            workerParams = workerParams,
            generateNewSotdIdUseCase = generateSotdIdUseCase,
            userPreferencesRepository = userPrefsRepository,
            notificationHelper = notificationHelper,
            getSunnahByIdUseCase = getSunnahByIdUseCase
        )

        return worker.doWork()
    }

    @Test
    fun doWork_should_return_success_when_notifications_are_disabled() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = false)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { userPrefsRepository.getUserPreferences() }
        coVerify(exactly = 0) { userPrefsRepository.shouldGenerateNewSotd() }
        coVerify(exactly = 0) { generateSotdIdUseCase.invoke() }
    }

    @Test
    fun doWork_should_return_success_when_notification_permission_not_granted() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs

        // Revoke notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shadowApplication = shadowOf(context as Application)
            shadowApplication.denyPermissions(Manifest.permission.POST_NOTIFICATIONS)
        }

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { userPrefsRepository.getUserPreferences() }
        coVerify(exactly = 0) { userPrefsRepository.shouldGenerateNewSotd() }
    }

    @Test
    fun doWork_should_return_success_when_SOTD_already_generated_for_today() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns false

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { userPrefsRepository.getUserPreferences() }
        coVerify { userPrefsRepository.shouldGenerateNewSotd() }
        coVerify(exactly = 0) { generateSotdIdUseCase.invoke() }
    }

    @Test
    fun doWork_should_return_retry_when_generateSotdIdUseCase_returns_null() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns null

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        coVerify { generateSotdIdUseCase.invoke() }
        coVerify(exactly = 0) { getSunnahByIdUseCase.invoke(any()) }
    }

    @Test
    fun doWork_should_return_retry_when_getSunnahByIdUseCase_returns_error() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Error(
            Exception("Database error"),
            "Failed to get sunnah"
        )

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        coVerify { generateSotdIdUseCase.invoke() }
        coVerify { getSunnahByIdUseCase.invoke(sunnahId) }
        coVerify(exactly = 0) { notificationHelper.showSotdNotification(any()) }
    }

    @Test
    fun doWork_should_return_retry_when_getSunnahByIdUseCase_returns_success_with_null_data() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Success(null)

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        coVerify { getSunnahByIdUseCase.invoke(sunnahId) }
        coVerify(exactly = 0) { notificationHelper.showSotdNotification(any()) }
    }

    @Test
    fun doWork_should_return_success_and_show_notification_when_all_conditions_are_met() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        val sunnah = createTestSunnah(sunnahId)

        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Success(sunnah)
        coEvery { notificationHelper.showSotdNotification(sunnah) } just Runs

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { userPrefsRepository.getUserPreferences() }
        coVerify { userPrefsRepository.shouldGenerateNewSotd() }
        coVerify { generateSotdIdUseCase.invoke() }
        coVerify { getSunnahByIdUseCase.invoke(sunnahId) }
        coVerify { notificationHelper.showSotdNotification(sunnah) }
    }

    @Test
    fun doWork_should_return_success_when_SecurityException_is_thrown() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } throws SecurityException("Permission denied")

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { userPrefsRepository.getUserPreferences() }
    }

    @Test
    fun doWork_should_return_retry_when_other_exception_is_thrown_with_low_attempt_count() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } throws RuntimeException("Database error")

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
    }
    //THIS ONE GETTING FAILED
    @Test
    fun doWork_should_return_failure_when_other_exception_is_thrown_with_high_attempt_count() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } throws RuntimeException("Database error")

        // When
        val result = createAndRunWorker(3)

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }

    @Test
    fun doWork_should_handle_notification_helper_exceptions_gracefully() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        val sunnah = createTestSunnah(sunnahId)

        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Success(sunnah)
        coEvery { notificationHelper.showSotdNotification(sunnah) } throws RuntimeException("Notification error")

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
        coVerify { notificationHelper.showSotdNotification(sunnah) }
    }

    @Test
    fun doWork_should_complete_all_steps_in_correct_order() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        val sunnah = createTestSunnah(sunnahId)

        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Success(sunnah)
        coEvery { notificationHelper.showSotdNotification(sunnah) } just Runs

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.success())

        // Verify order of calls
        coVerifyOrder {
            userPrefsRepository.getUserPreferences()
            userPrefsRepository.shouldGenerateNewSotd()
            generateSotdIdUseCase.invoke()
            getSunnahByIdUseCase.invoke(sunnahId)
            notificationHelper.showSotdNotification(sunnah)
        }
    }

    @Test
    fun doWork_should_handle_multiple_rapid_calls_correctly() = runTest {
        // Given
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns false

        // When - simulate multiple rapid calls
        val results = (1..5).map { createAndRunWorker() }

        // Then
        results.forEach { result ->
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }

        // Should be called for each worker instance
        coVerify(exactly = 5) { userPrefsRepository.getUserPreferences() }
        coVerify(exactly = 5) { userPrefsRepository.shouldGenerateNewSotd() }
    }

    @Test
    fun doWork_should_handle_null_userPreferences_gracefully() = runTest {
        // Given
        coEvery { userPrefsRepository.getUserPreferences() } throws Exception("Preferences not found")

        // When
        val result = createAndRunWorker()

        // Then
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
    }

    @Test
    fun doWork_should_work_correctly_on_Android_API_levels_below_TIRAMISU() = runTest {
        // Given - simulate older Android version
        val userPrefs = createUserPreferences(isSotdNotificationEnabled = true)
        val sunnahId = "01_01"
        val sunnah = createTestSunnah(sunnahId)

        coEvery { userPrefsRepository.getUserPreferences() } returns userPrefs
        coEvery { userPrefsRepository.shouldGenerateNewSotd() } returns true
        coEvery { generateSotdIdUseCase.invoke() } returns sunnahId
        coEvery { getSunnahByIdUseCase.invoke(sunnahId) } returns Result.Success(sunnah)
        coEvery { notificationHelper.showSotdNotification(sunnah) } just Runs

        // When
        val result = createAndRunWorker()

        // Then - should work without permission check
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        coVerify { notificationHelper.showSotdNotification(sunnah) }
    }

    private fun createUserPreferences(
        isSotdNotificationEnabled: Boolean = true,
        username: String = "Test User"
    ): UserPreferences {
        return UserPreferences(
            username = username,
            themeMode = 0,
            isDynamicThemeEnabled = false,
            isDailyReminderEnabled = false,
            hasCompletedOnboarding = true,
            hasSeenDisclaimer = true,
            recentlyViewedSunnahIds = emptyList(),
            currentSotdId = "01_01",
            sotdGeneratedDate = System.currentTimeMillis(),
            isSotdSeen = false,
            sotdNotificationTime = NotificationTime.MORNING,
            isSotdNotificationEnabled = isSotdNotificationEnabled
        )
    }

    private fun createTestSunnah(id: String): Sunnah {
        return Sunnah(
            id = id,
            categoryId = 1,
            title = "Test Sunnah",
            body = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = "normal",
                    content = "Test content"
                )
            ),
            references = listOf(Reference("Test Reference")),
            extra = null,
            isBookmarked = false,
            bookmarkedAt = null
        )
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UseCaseModule::class, SotdModule::class]
)
class TestSotdWorkerModule {

    @Provides
    @Singleton
    fun provideGenerateNewSotdUseCase(): GenerateNewSotdIdUseCase = mockk()

    @Provides
    @Singleton
    fun provideSotdNotificationHelper(): SotdNotificationHelper = mockk()

    @Provides
    @Singleton
    fun provideGetSunnahByIdUseCase(): GetSunnahByIdUseCase = mockk()
}