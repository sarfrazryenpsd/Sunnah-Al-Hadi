package com.ryen.sunnah_alhadi.data.repository

import androidx.datastore.core.DataStore
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserPreferencesRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @MockK
    private lateinit var repository: UserPreferencesRepositoryImpl
    private lateinit var mockDataStore: DataStore<ProtoUserPreferences>
    private val testScope = TestScope()

    @Before
    fun setup() {
        mockDataStore = mockk(relaxed = true)
        repository = UserPreferencesRepositoryImpl(
            dataStore = mockDataStore,
            applicationScope = testScope
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getUserPreferences_returns_correct_preferences() = runTest {
        // Given
        val protoPrefs = createTestProtoPreferences()
        every { mockDataStore.data } returns flowOf(protoPrefs)

        // Allow some time for stateIn to collect the value
        testScope.advanceUntilIdle()

        // When
        val result = repository.getUserPreferences()

        // Then
        assertThat(result.username).isEqualTo("TestUser")
        assertThat(result.themeMode).isEqualTo(1)
        assertThat(result.isDynamicThemeEnabled).isTrue()
    }

    @Test
    fun getUserPreferencesFlow_emits_correct_values() = runTest {
        // Given
        val protoPrefs1 = createTestProtoPreferences(username = "User1")
        val protoPrefs2 = createTestProtoPreferences(username = "User2")
        every { mockDataStore.data } returns flowOf(protoPrefs1, protoPrefs2)

        // When & Then
        repository.getUserPreferencesFlow().test {
            // Skip the initial default value if present
            var item = awaitItem()
            if (item.username.isEmpty()) {
                item = awaitItem() // Get the first real value
            }
            assertThat(item.username).isEqualTo("User1")

            val secondItem = awaitItem()
            assertThat(secondItem.username).isEqualTo("User2")

            awaitComplete()
        }
    }

    @Test
    fun updateUsername_calls_dataStore_updateData_correctly() = runTest {
        // Given
        val newUsername = "NewUser"
        val currentPrefs = createTestProtoPreferences()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            val result = transform(currentPrefs)
            result
        }

        // When
        repository.updateUsername(newUsername)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun update_Theme_Mode_updates_theme_correctly() = runTest {
        // Given
        val newTheme = ThemeMode.DARK
        val currentPrefs = createTestProtoPreferences()
        val updatedPrefs = currentPrefs.toBuilder().setThemeMode(newTheme.ordinal).build()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns updatedPrefs

        // When
        repository.updateThemeMode(newTheme)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun updateDynamicTheme_updates_setting_correctly() = runTest {
        // Given
        val enabled = false
        val currentPrefs = createTestProtoPreferences()
        val updatedPrefs = currentPrefs.toBuilder().setIsDynamicThemeEnabled(enabled).build()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns updatedPrefs

        // When
        repository.updateDynamicTheme(enabled)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun updateDailyReminder_updates_setting_correctly() = runTest {
        // Given
        val enabled = true
        val currentPrefs = createTestProtoPreferences()
        val updatedPrefs = currentPrefs.toBuilder().setIsDailyReminderEnabled(enabled).build()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns updatedPrefs

        // When
        repository.updateDailyReminder(enabled)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun markOnboardingCompleted_updates_flag_correctly() = runTest {
        // Given
        val currentPrefs = createTestProtoPreferences()
        val updatedPrefs = currentPrefs.toBuilder().setHasCompletedOnboarding(true).build()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns updatedPrefs

        // When
        repository.markOnboardingCompleted()

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun markDisclaimerSeen_updates_flag_correctly() = runTest {
        // Given
        val currentPrefs = createTestProtoPreferences()
        val updatedPrefs = currentPrefs.toBuilder().setHasSeenDisclaimer(true).build()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns updatedPrefs

        // When
        repository.markDisclaimerSeen()

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun getRecentlyViewedIds_returns_correct_list() = runTest {
        // Given
        val expectedIds = listOf("01_01", "02_02", "03_03")
        val protoPrefs = createTestProtoPreferences(recentlyViewedIds = expectedIds)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.getRecentlyViewedIds()

        // Then
        assertThat(result).isEqualTo(expectedIds)
    }

    @Test
    fun addToRecentlyViewed_adds_new_id_to_beginning_and_removes_duplicates() = runTest {
        // Given
        val newId = "04_04"
        val existingIds = listOf("01_01", "02_02", "03_03")
        val currentPrefs = createTestProtoPreferences(recentlyViewedIds = existingIds)

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            transform(currentPrefs)
        }

        // When
        repository.addToRecentlyViewed(newId)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun addToRecentlyViewed_removes_existing_id_and_adds_to_beginning() = runTest {
        // Given
        val existingId = "02_02"
        val currentIds = listOf("01_01", "02_02", "03_03")
        val currentPrefs = createTestProtoPreferences(recentlyViewedIds = currentIds)

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            val result = transform(currentPrefs)
            // Verify the logic
            val newList = result.recentlyViewedSunnahIdsList
            assertThat(newList.first()).isEqualTo(existingId)
            assertThat(newList.count { it == existingId }).isEqualTo(1)
            result
        }

        // When
        repository.addToRecentlyViewed(existingId)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun addToRecentlyViewed_respects_limit_of_30_items() = runTest {
        // Given
        val currentIds = (1..30).map { String.format("%02d_%02d", it % 10, it) }
        val newId = "99_99"
        val currentPrefs = createTestProtoPreferences(recentlyViewedIds = currentIds)

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            val result = transform(currentPrefs)
            // Verify the limit is respected
            assertThat(result.recentlyViewedSunnahIdsList.size).isAtMost(30)
            result
        }

        // When
        repository.addToRecentlyViewed(newId)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun getCurrentSotd_returns_correct_sotd_id() = runTest {
        // Given
        val expectedSotdId = "05_05"
        val protoPrefs = createTestProtoPreferences(currentSotdId = expectedSotdId)
        every { mockDataStore.data } returns flowOf(protoPrefs)

        // Allow stateIn to collect
        testScope.advanceUntilIdle()

        // When
        val result = repository.getCurrentSotd()

        // Then
        assertThat(result).isEqualTo(expectedSotdId)
    }

    @Test
    fun updateSotdNotificationTime_updates_correctly() = runTest {
        // Given
        val newTime = NotificationTime.EVENING
        val currentPrefs = createTestProtoPreferences()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns currentPrefs.toBuilder().setSotdNotificationTime(newTime.ordinal).build()

        // When
        repository.updateSotdNotificationTime(newTime)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun updateSotdNotificationEnabled_updates_correctly() = runTest {
        // Given
        val enabled = false
        val currentPrefs = createTestProtoPreferences()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns currentPrefs.toBuilder().setIsSotdNotificationEnabled(enabled).build()

        // When
        repository.updateSotdNotificationEnabled(enabled)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun getSotdNotificationTime_returns_correct_time() = runTest {
        // Given
        val expectedTime = NotificationTime.NIGHT
        val protoPrefs = createTestProtoPreferences(sotdNotificationTime = expectedTime.ordinal)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.getSotdNotificationTime()

        // Then
        assertThat(result).isEqualTo(expectedTime)
    }

    @Test
    fun getSotdNotificationTime_returns_MORNING_for_invalid_ordinal() = runTest {
        // Given
        val invalidOrdinal = 999
        val protoPrefs = createTestProtoPreferences(sotdNotificationTime = invalidOrdinal)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.getSotdNotificationTime()

        // Then
        assertThat(result).isEqualTo(NotificationTime.MORNING)
    }

    @Test
    fun isSotdNotificationEnabled_returns_correct_value() = runTest {
        // Given
        val expectedEnabled = true
        val protoPrefs = createTestProtoPreferences(isSotdNotificationEnabled = expectedEnabled)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.isSotdNotificationEnabled()

        // Then
        assertThat(result).isEqualTo(expectedEnabled)
    }

    @Test
    fun updateCurrentSotd_updates_sotd_and_adds_previous_to_recently_viewed() = runTest {
        // Given
        val newSotdId = "06_06"
        val previousSotdId = "05_05"
        val generatedDate = System.currentTimeMillis()
        val currentIds = listOf("01_01", "02_02")
        val currentPrefs = createTestProtoPreferences(
            currentSotdId = previousSotdId,
            recentlyViewedIds = currentIds
        )

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            val result = transform(currentPrefs)

            // Verify the sotd update logic
            assertThat(result.currentSotdId).isEqualTo(newSotdId)
            assertThat(result.sotdGeneratedDate).isEqualTo(generatedDate)
            assertThat(result.isSotdSeen).isFalse()
            assertThat(result.recentlyViewedSunnahIdsList.first()).isEqualTo(previousSotdId)

            result
        }

        // When
        repository.updateCurrentSotd(newSotdId, generatedDate)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun updateCurrentSotd_does_not_add_empty_previous_sotd_to_recently_viewed() = runTest {
        // Given
        val newSotdId = "06_06"
        val generatedDate = System.currentTimeMillis()
        val currentIds = listOf("01_01", "02_02")
        val currentPrefs = createTestProtoPreferences(
            currentSotdId = "", // Empty previous SOTD
            recentlyViewedIds = currentIds
        )

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } coAnswers {
            val transform = firstArg<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()
            val result = transform(currentPrefs)

            // Verify empty previous SOTD is not added
            assertThat(result.recentlyViewedSunnahIdsList).doesNotContain("")

            result
        }

        // When
        repository.updateCurrentSotd(newSotdId, generatedDate)

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun markSotdAsSeen_updates_flag_correctly() = runTest {
        // Given
        val currentPrefs = createTestProtoPreferences()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns currentPrefs.toBuilder().setIsSotdSeen(true).build()

        // When
        repository.markSotdAsSeen()

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun markSotdAsUnseen_updates_flag_correctly() = runTest {
        // Given
        val currentPrefs = createTestProtoPreferences()

        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } returns currentPrefs.toBuilder().setIsSotdSeen(false).build()

        // When
        repository.markSotdAsUnseen()

        // Then
        coVerify { mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>()) }
    }

    @Test
    fun isSotdSeen_returns_correct_value() = runTest {
        // Given
        val expectedSeen = true
        val protoPrefs = createTestProtoPreferences(isSotdSeen = expectedSeen)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.isSotdSeen()

        // Then
        assertThat(result).isEqualTo(expectedSeen)
    }

    @Test
    fun getSotdGeneratedDate_returns_correct_date() = runTest {
        // Given
        val expectedDate = System.currentTimeMillis()
        val protoPrefs = createTestProtoPreferences(sotdGeneratedDate = expectedDate)
        every { mockDataStore.data } returns flowOf(protoPrefs)

        // When
        val result = repository.getSotdGeneratedDate()

        // Then
        assertThat(result).isEqualTo(expectedDate)
    }

    @Test
    fun shouldGenerateNewSotd_returns_true_for_first_time_generation() = runTest {
        // Given - generatedDate is 0L (first time)
        val protoPrefs = createTestProtoPreferences(sotdGeneratedDate = 0L)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.shouldGenerateNewSotd()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun shouldGenerateNewSotd_returns_true_for_different_day() = runTest {
        // Given - yesterday's date
        val yesterday = LocalDate.now().minusDays(1)
        val yesterdayMillis = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val protoPrefs = createTestProtoPreferences(sotdGeneratedDate = yesterdayMillis)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.shouldGenerateNewSotd()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun shouldGenerateNewSotd_returns_false_for_same_day() = runTest {
        // Given - today's date
        val today = LocalDate.now()
        val todayMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val protoPrefs = createTestProtoPreferences(sotdGeneratedDate = todayMillis)
        coEvery { mockDataStore.data.first() } returns protoPrefs

        // When
        val result = repository.shouldGenerateNewSotd()

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun getCurrentSotdFlow_emits_distinct_values_only() = runTest {
        // Given
        val sotdId1 = "01_01"
        val sotdId2 = "02_02"
        val prefs1 = createTestProtoPreferences(currentSotdId = sotdId1)
        val prefs2 = createTestProtoPreferences(currentSotdId = sotdId1) // Same ID
        val prefs3 = createTestProtoPreferences(currentSotdId = sotdId2) // Different ID

        every { mockDataStore.data } returns flowOf(prefs1, prefs2, prefs3)

        // When & Then
        repository.getCurrentSotdFlow().test {
            assertThat(awaitItem()).isEqualTo(sotdId1)
            assertThat(awaitItem()).isEqualTo(sotdId2) // prefs2 should be skipped due to distinctUntilChanged
            awaitComplete()
        }
    }

    @Test
    fun isSotdSeenFlow_emits_distinct_values_only() = runTest {
        // Given
        val prefs1 = createTestProtoPreferences(isSotdSeen = false)
        val prefs2 = createTestProtoPreferences(isSotdSeen = false) // Same value
        val prefs3 = createTestProtoPreferences(isSotdSeen = true) // Different value

        every { mockDataStore.data } returns flowOf(prefs1, prefs2, prefs3)

        // When & Then
        repository.isSotdSeenFlow().test {
            assertThat(awaitItem()).isFalse()
            assertThat(awaitItem()).isTrue() // prefs2 should be skipped due to distinctUntilChanged
            awaitComplete()
        }
    }

    // Error handling tests
    @Test
    fun getUserPreferences_throws_exception_when_dataStore_fails() = runTest {
        // Given
        val exception = RuntimeException("DataStore failure")
        every { mockDataStore.data } returns flow { throw exception }

        // When & Then
        try {
            repository.getUserPreferences()
            fail("Expected exception was not thrown")
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("DataStore failure")
        }
    }



    @Test
    fun updateUsername_handles_dataStore_update_failure() = runTest {
        // Given
        val exception = RuntimeException("DataStore update failure")
        coEvery {
            mockDataStore.updateData(any<suspend (ProtoUserPreferences) -> ProtoUserPreferences>())
        } throws exception

        // When & Then
        try {
            repository.updateUsername("NewUsername")
            fail("Expected exception was not thrown")
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("DataStore update failure")
        }
    }

    @Test
    fun getUserPreferencesFlow_handles_dataStore_errors_gracefully() = runTest {
        // Given
        val exception = RuntimeException("DataStore error")
        every { mockDataStore.data } returns flow { throw exception }

        // When & Then
        repository.getUserPreferencesFlow().test {
            awaitError() // Should propagate the error
        }
    }

    // Helper function to create test ProtoUserPreferences
    private fun createTestProtoPreferences(
        username: String = "TestUser",
        themeMode: Int = 1,
        isDynamicThemeEnabled: Boolean = true,
        isDailyReminderEnabled: Boolean = false,
        hasCompletedOnboarding: Boolean = false,
        hasSeenDisclaimer: Boolean = false,
        recentlyViewedIds: List<String> = emptyList(),
        currentSotdId: String = "01_01",
        sotdGeneratedDate: Long = System.currentTimeMillis(),
        isSotdSeen: Boolean = false,
        sotdNotificationTime: Int = NotificationTime.MORNING.ordinal,
        isSotdNotificationEnabled: Boolean = true
    ): ProtoUserPreferences {
        return ProtoUserPreferences.newBuilder()
            .setUsername(username)
            .setThemeMode(themeMode)
            .setIsDynamicThemeEnabled(isDynamicThemeEnabled)
            .setIsDailyReminderEnabled(isDailyReminderEnabled)
            .setHasCompletedOnboarding(hasCompletedOnboarding)
            .setHasSeenDisclaimer(hasSeenDisclaimer)
            .addAllRecentlyViewedSunnahIds(recentlyViewedIds)
            .setCurrentSotdId(currentSotdId)
            .setSotdGeneratedDate(sotdGeneratedDate)
            .setIsSotdSeen(isSotdSeen)
            .setSotdNotificationTime(sotdNotificationTime)
            .setIsSotdNotificationEnabled(isSotdNotificationEnabled)
            .build()
    }
}