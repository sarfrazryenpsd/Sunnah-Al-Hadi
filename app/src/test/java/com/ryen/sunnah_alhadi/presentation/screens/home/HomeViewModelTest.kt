@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.MainDispatcherRule
import com.ryen.sunnah_alhadi.domain.model.SotdState
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetRecentlyViewedSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.HomeData
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class HomeViewModelTest {

    @get:Rule
    val coroutinesTestRule = MainDispatcherRule()

    // Mocks
    private val getHomeDataUseCase: GetHomeDataUseCase = mockk()
    private val getCurrentSotdUseCase: GetCurrentSotdUseCase = mockk()
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase = mockk()
    private val getSunnahCountsUseCase: GetSunnahCountsUseCase = mockk()
    private val getRecentlyViewedSunnahsUseCase: GetRecentlyViewedSunnahsUseCase = mockk()
    private val shouldShowSotdCardUseCase: ShouldShowSotdCardUseCase = mockk()
    private val markSotdAsSeenUseCase: MarkSotdAsSeenUseCase = mockk()
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase = mockk()

    private lateinit var viewModel: HomeViewModel

    private val sampleSunnah = Sunnah("id1", 1, "title", emptyList())

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        // Defaults
        coEvery { getHomeDataUseCase() } returns Result.Success(
            HomeData(userName = "testUser", featuredCategories = emptyList())
        )
        coEvery { getCurrentSotdUseCase() } returns SotdState(null,
            isSeen = false,
            isAvailable = false,
            generatedDate = 0L
        )
        coEvery { getUserPreferencesFlowUseCase() } returns flowOf(UserPreferences("user1"))
        coEvery { getSunnahCountsUseCase(any()) } returns emptyMap()
        coEvery { getRecentlyViewedSunnahsUseCase() } returns flowOf(emptyList())

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )
    }

    @Test
    fun `getUiState returns correct initial state`() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.username).isEqualTo("testUser")
    }

    @Test
    fun `getEventFlow emits no events initially`() = runTest {
        viewModel.eventFlow.test {
            expectNoEvents()
        }
    }

    @Test
    fun `getSotdOverlayRequest emits no requests initially`() = runTest {
        viewModel.sotdOverlayRequest.test {
            expectNoEvents()
        }
    }

    @Test
    fun `onEvent ToggleSotd emits Manual SOTD overlay request`() = runTest {
        viewModel.sotdOverlayRequest.test {
            viewModel.onEvent(HomeEvent.ToggleSotd)
            assertThat(awaitItem()).isEqualTo(SotdOverlayRequest.Manual)
        }
    }

    @Test
    fun `onEvent MarkSotdAsSeen calls use case successfully`() = runTest {
        viewModel.onEvent(HomeEvent.MarkSotdAsSeen)
        advanceUntilIdle()
        coVerify(exactly = 1) { markSotdAsSeenUseCase() }
    }

    @Test
    fun `onEvent MarkSotdAsSeen handles use case failure`() = runTest {
        coEvery { markSotdAsSeenUseCase() } throws RuntimeException("fail")
        viewModel.onEvent(HomeEvent.MarkSotdAsSeen)
        advanceUntilIdle()
        coVerify { markSotdAsSeenUseCase() }
        // No crash, state unchanged
        assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun `onEvent ToggleDisclaimer flips showDisclaimer state`() = runTest {
        assertThat(viewModel.uiState.value.showDisclaimer).isFalse()

        viewModel.onEvent(HomeEvent.ToggleDisclaimer)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.showDisclaimer).isTrue()

        viewModel.onEvent(HomeEvent.ToggleDisclaimer)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.showDisclaimer).isFalse()
    }


    @Test
    fun `onEvent AutoShowSotdCheck does not emit when shouldShow is false`() = runTest {
        coEvery { shouldShowSotdCardUseCase() } returns false
        coEvery { getCurrentSotdUseCase() } returns SotdState(sampleSunnah,
            isSeen = false,
            isAvailable = true,
            generatedDate = 0L
        )

        viewModel.sotdOverlayRequest.test {
            viewModel.onEvent(HomeEvent.AutoShowSotdCheck)
            expectNoEvents()
        }
    }

    @Test
    fun `onEvent AutoShowSotdCheck does not emit when SOTD is null`() = runTest {
        coEvery { shouldShowSotdCardUseCase() } returns true
        coEvery { getCurrentSotdUseCase() } returns SotdState(null,
            isSeen = false,
            isAvailable = false,
            generatedDate = 0L
        )

        viewModel.sotdOverlayRequest.test {
            viewModel.onEvent(HomeEvent.AutoShowSotdCheck)
            expectNoEvents()
        }
    }

    @Test
    fun `onEvent AutoShowSotdCheck does not emit when SOTD is already seen`() = runTest {
        coEvery { shouldShowSotdCardUseCase() } returns true
        coEvery { getCurrentSotdUseCase() } returns SotdState(sampleSunnah,
            isSeen = true,
            isAvailable = true,
            generatedDate = 0L
        )

        viewModel.sotdOverlayRequest.test {
            viewModel.onEvent(HomeEvent.AutoShowSotdCheck)
            expectNoEvents()
        }
    }

    @Test
    fun `onEvent AutoShowSotdCheck handles use case exceptions`() = runTest {
        coEvery { shouldShowSotdCardUseCase() } throws RuntimeException("oops")

        viewModel.onEvent(HomeEvent.AutoShowSotdCheck)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.error).contains("Failed to check SOTD")
    }

    @Test
    fun `onEvent SunnahCardClicked updates pager visibility and index`() = runTest {
        viewModel.onEvent(HomeEvent.SunnahCardClicked(2))
        val state = viewModel.uiState.value
        assertThat(state.isPagerVisible).isTrue()
        assertThat(state.selectedSunnahIndex).isEqualTo(2)
    }

    @Test
    fun `onEvent ClosePager updates pager visibility`() = runTest {
        viewModel.onEvent(HomeEvent.SunnahCardClicked(1))
        assertThat(viewModel.uiState.value.isPagerVisible).isTrue()
        viewModel.onEvent(HomeEvent.ClosePager)
        assertThat(viewModel.uiState.value.isPagerVisible).isFalse()
    }

    @Test
    fun `onEvent PagerPageChanged updates selected index`() = runTest {
        viewModel.onEvent(HomeEvent.PagerPageChanged(5))
        assertThat(viewModel.uiState.value.selectedSunnahIndex).isEqualTo(5)
    }

    @Test
    fun `handleNotificationLaunch does not emit request when no SOTD`() = runTest {
        coEvery { getCurrentSotdUseCase() } returns SotdState(null,
            isSeen = false,
            isAvailable = false,
            generatedDate = 0L
        )

        viewModel.sotdOverlayRequest.test {
            viewModel.handleNotificationLaunch()
            expectNoEvents()
        }
    }




    @Test
    fun `init loads sunnah counts successfully`() = runTest {
        coEvery { getSunnahCountsUseCase(any()) } returns mapOf(1 to 5)
        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.sunnahCount[1]).isEqualTo(5)
    }

    @Test
    fun `init handles sunnah count load failure`() = runTest {
        coEvery { getSunnahCountsUseCase(any()) } throws RuntimeException("fail")
        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.sunnahCount).isEmpty()
    }

    @Test
    fun `init handles getHomeDataUseCase error`() = runTest {
        coEvery { getHomeDataUseCase() } returns Result.Error(Exception("bad"))

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )

        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.error).contains("bad")
    }

    @Test
    fun `init handles overall data loading exception`() = runTest {
        // Instead of throwing, return a Result.Error
        coEvery { getHomeDataUseCase() } returns Result.Error(RuntimeException("boom"))

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )

        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).contains("boom")
    }


    @Test
    fun `user preference observation handles exceptions`() = runTest {
        coEvery { getUserPreferencesFlowUseCase() } returns flow {
            delay(1)
            throw RuntimeException("prefs fail")
        }

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )

        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).contains("prefs fail")
    }



    @Test
    fun `user preference change triggers SOTD refresh`() = runTest {
        coEvery { getUserPreferencesFlowUseCase() } returns flowOf(UserPreferences("u1"))
        coEvery { getCurrentSotdUseCase() } returns SotdState(sampleSunnah,
            isSeen = false,
            isAvailable = true,
            generatedDate = 0L
        )

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.sotd).isEqualTo(sampleSunnah)
    }

    @Test
    fun `user preference change triggers auto show for new SOTD`() = runTest {
        val old = Sunnah("old", 1, "old", emptyList())
        val new = Sunnah("new", 1, "new", emptyList())
        coEvery { getCurrentSotdUseCase() } returnsMany listOf(
            SotdState(old, isSeen = false, isAvailable = true, generatedDate = 0L),
            SotdState(new, isSeen = false, isAvailable = true, generatedDate = 0L)
        )
        coEvery { getUserPreferencesFlowUseCase() } returns flowOf(UserPreferences("u2"))

        viewModel = HomeViewModel(
            getHomeDataUseCase,
            getCurrentSotdUseCase,
            getUserPreferencesFlowUseCase,
            getSunnahCountsUseCase,
            getRecentlyViewedSunnahsUseCase,
            shouldShowSotdCardUseCase,
            markSotdAsSeenUseCase,
            toggleBookmarkUseCase
        )

        viewModel.sotdOverlayRequest.test {
            assertThat(awaitItem()).isEqualTo(SotdOverlayRequest.AutoShow)
        }
    }

    @Test
    fun `toggleBookmark fails and updates error state`() = runTest {
        coEvery { toggleBookmarkUseCase(any()) } throws RuntimeException("fail bookmark")
        viewModel.onEvent(HomeEvent.ToggleBookmark("id1"))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.error).contains("Failed to toggle bookmark")
    }

    @Test
    fun `onEvent DismissSotd does nothing`() = runTest {
        val before = viewModel.uiState.value
        viewModel.onEvent(HomeEvent.DismissSotd)
        val after = viewModel.uiState.value
        assertThat(before).isEqualTo(after)
    }

    @Test
    fun `concurrent onEvent calls are handled correctly`() = runTest {
        viewModel.eventFlow.test {
            viewModel.onEvent(HomeEvent.ToggleBookmark("id1"))
            viewModel.onEvent(HomeEvent.NavigateToTopic(99))

            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToTopic(99))
        }
    }
}
