package com.ryen.sunnah_alhadi.data.repository

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.util.Result
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BookmarkRepositoryTest {

    @MockK
    private lateinit var bookmarkDao: BookmarkDao

    private lateinit var repository: BookmarkRepositoryImpl
    private lateinit var ioDispatcher: CoroutineDispatcher

    private val testSunnahEntity = SunnahEntity(
        id = "01_01",
        categoryId = 1,
        title = "Test Sunnah",
        body = listOf(
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.NORMAL,
                content = "Test content"
            )
        ),
        references = null,
        extra = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        ioDispatcher = UnconfinedTestDispatcher()
        repository = BookmarkRepositoryImpl(bookmarkDao, ioDispatcher)

        // Mock Android Log to avoid AndroidRuntimeException
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    // getBookmarkedSunnahsFlow tests
    @Test
    fun getBookmarkedSunnahsFlow_success_emitsSuccessResults() = runTest {
        // Given
        val flowData = flowOf(listOf(testSunnahEntity))
        every { bookmarkDao.getBookmarkedSunnahsFlow() } returns flowData

        // When & Then
        repository.getBookmarkedSunnahsFlow().test {
            val emission = awaitItem()
            Truth.assertThat(emission).isInstanceOf(Result.Success::class.java)
            val successResult = emission as Result.Success
            Truth.assertThat(successResult.data).hasSize(1)
            Truth.assertThat(successResult.data[0].isBookmarked).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun getBookmarkedSunnahsFlow_databaseError_emitsErrorResult() = runTest {
        // Given
        val exception = SQLiteException("Database error")
        val errorFlow = flow<List<SunnahEntity>> { throw exception }
        every { bookmarkDao.getBookmarkedSunnahsFlow() } returns errorFlow

        // When & Then
        repository.getBookmarkedSunnahsFlow().test {
            val emission = awaitItem()
            Truth.assertThat(emission).isInstanceOf(Result.Error::class.java)
            val errorResult = emission as Result.Error
            Truth.assertThat(errorResult.exception).isEqualTo(exception)
            Truth.assertThat(errorResult.message).isEqualTo("Database connection error")
            awaitComplete()
        }
    }

    // toggleBookmark tests
    @Test
    fun toggleBookmark_successScenarios_returnsCorrectResults() = runTest {
        // Test adding bookmark (not bookmarked -> bookmarked)
        coEvery { bookmarkDao.isBookmarked("01_01") } returns false
        coEvery { bookmarkDao.addBookmark(any()) } just Runs

        val addResult = repository.toggleBookmark("01_01")
        Truth.assertThat(addResult).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((addResult as Result.Success).data).isTrue()

        // Test removing bookmark (bookmarked -> not bookmarked)
        coEvery { bookmarkDao.isBookmarked("01_01") } returns true
        coEvery { bookmarkDao.removeBookmark("01_01") } just Runs

        val removeResult = repository.toggleBookmark("01_01")
        Truth.assertThat(removeResult).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((removeResult as Result.Success).data).isFalse()
    }

    @Test
    fun toggleBookmark_databaseErrors_returnsErrorResults() = runTest {
        // Test database check failure
        val checkException = SQLException("Check failed")
        coEvery { bookmarkDao.isBookmarked("01_01") } throws checkException

        val checkResult = repository.toggleBookmark("01_01")
        Truth.assertThat(checkResult).isInstanceOf(Result.Error::class.java)
        Truth.assertThat((checkResult as Result.Error).exception).isEqualTo(checkException)

        // Test add bookmark failure
        coEvery { bookmarkDao.isBookmarked("01_02") } returns false
        val addException = SQLiteConstraintException("Add failed")
        coEvery { bookmarkDao.addBookmark(any()) } throws addException

        val addResult = repository.toggleBookmark("01_02")
        Truth.assertThat(addResult).isInstanceOf(Result.Error::class.java)
        Truth.assertThat((addResult as Result.Error).exception).isEqualTo(addException)

        // Test remove bookmark failure
        coEvery { bookmarkDao.isBookmarked("01_03") } returns true
        val removeException = SQLException("Remove failed")
        coEvery { bookmarkDao.removeBookmark("01_03") } throws removeException

        val removeResult = repository.toggleBookmark("01_03")
        Truth.assertThat(removeResult).isInstanceOf(Result.Error::class.java)
        Truth.assertThat((removeResult as Result.Error).exception).isEqualTo(removeException)
    }
}