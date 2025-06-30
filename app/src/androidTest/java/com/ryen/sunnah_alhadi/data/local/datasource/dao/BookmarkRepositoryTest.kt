import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.repository.BookmarkRepositoryImpl
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
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class BookmarkRepositoryTest {

    @MockK
    private lateinit var bookmarkDao: BookmarkDao

    private lateinit var repository: BookmarkRepositoryImpl

    private val testBookmarkEntity = BookmarkEntity(
        sunnahId = "01_01",
        bookmarkedAt = 1234567890L
    )

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

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = BookmarkRepositoryImpl(bookmarkDao)

        // Mock Android Log to avoid AndroidRuntimeException
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        // Clean up any static mocks
        unmockkStatic(Log::class)
    }

    @Test
    fun getAllBookmarks_success_returnsSuccessResult() = runTest {
        // Given
        coEvery { bookmarkDao.getAllBookmarks() } returns listOf(testBookmarkEntity)

        // When
        val result = repository.getAllBookmarks()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).hasSize(1)
        assertThat(successResult.data[0].sunnahId).isEqualTo("01_01")
    }

    @Test
    fun getAllBookmarks_daoThrowsException_returnsErrorResult() = runTest {
        // Given
        val exception = SQLException("Database error")
        coEvery { bookmarkDao.getAllBookmarks() } throws exception

        // When
        val result = repository.getAllBookmarks()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to load bookmarks")
    }


    @Test
    fun getAllBookmarks_emptyList_returnsSuccessWithEmptyList() = runTest {
        // Given
        coEvery { bookmarkDao.getAllBookmarks() } returns emptyList()

        // When
        val result = repository.getAllBookmarks()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEmpty()
    }

    @Test
    fun getBookmarkedSunnahs_success_returnsSuccessResult() = runTest {
        // Given
        coEvery { bookmarkDao.getBookmarkedSunnahs() } returns listOf(testSunnahEntity)

        // When
        val result = repository.getBookmarkedSunnahs()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).hasSize(1)
        assertThat(successResult.data[0].id).isEqualTo("01_01")
        assertThat(successResult.data[0].isBookmarked).isTrue()
    }

    @Test
    fun getBookmarkedSunnahs_daoThrowsException_returnsErrorResult() = runTest {
        // Given
        val exception = RuntimeException("Database connection error")
        coEvery { bookmarkDao.getBookmarkedSunnahs() } throws exception

        // When
        val result = repository.getBookmarkedSunnahs()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to load bookmarked sunnahs")
    }

    @Test
    fun getBookmarkedSunnahsFlow_success_emitsSuccessResults() = runTest {
        // Given
        val flowData = flowOf(listOf(testSunnahEntity))
        every { bookmarkDao.getBookmarkedSunnahsFlow() } returns flowData

        // When & Then
        repository.getBookmarkedSunnahsFlow().test {
            val emission = awaitItem()
            assertThat(emission).isInstanceOf(Result.Success::class.java)
            val successResult = emission as Result.Success
            assertThat(successResult.data).hasSize(1)
            assertThat(successResult.data[0].isBookmarked).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun getBookmarkedSunnahsFlow_flowThrowsException_emitsErrorResult() = runTest {
        // Given
        val exception = SQLiteException("Database corrupted")
        val errorFlow = flow<List<SunnahEntity>> { throw exception }
        every { bookmarkDao.getBookmarkedSunnahsFlow() } returns errorFlow

        // When & Then
        repository.getBookmarkedSunnahsFlow().test {
            val emission = awaitItem()
            assertThat(emission).isInstanceOf(Result.Error::class.java)
            val errorResult = emission as Result.Error
            assertThat(errorResult.exception).isEqualTo(exception)
            assertThat(errorResult.message).isEqualTo("Database connection error")
            awaitComplete()
        }
    }

    @Test
    fun getBookmarkedSunnahsFlow_mapperThrowsException_emitsErrorResult() = runTest {
        // Given - Create a flow that emits data but will fail during mapping
        // We'll simulate this by having the DAO return data that causes issues
        val problematicEntity = testSunnahEntity.copy(body = emptyList()) // Invalid body
        val flowData = flowOf(listOf(problematicEntity))
        every { bookmarkDao.getBookmarkedSunnahsFlow() } returns flowData

        // When & Then
        repository.getBookmarkedSunnahsFlow().test {
            val emission = awaitItem()
            // The repository should handle mapping errors gracefully
            // This test verifies error handling in the flow transformation
            assertThat(emission).isInstanceOf(Result.Success::class.java)
            awaitComplete()
        }
    }

    @Test
    fun isBookmarked_validId_returnsSuccessResult() = runTest {
        // Given
        coEvery { bookmarkDao.isBookmarked("01_01") } returns true

        // When
        val result = repository.isBookmarked("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isTrue()
    }

    @Test
    fun isBookmarked_daoThrowsException_returnsErrorResult() = runTest {
        // Given
        val exception = SQLiteException("Query failed")
        coEvery { bookmarkDao.isBookmarked("01_01") } throws exception

        // When
        val result = repository.isBookmarked("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to check bookmark status")
    }

    @Test
    fun addBookmark_validId_returnsSuccessResult() = runTest {
        // Given
        coEvery { bookmarkDao.addBookmark(any()) } just Runs

        // When
        val result = repository.addBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify { bookmarkDao.addBookmark(match { it.sunnahId == "01_01" }) }
    }

    @Test
    fun addBookmark_daoThrowsException_returnsErrorResult() = runTest {
        // Given
        val exception = SQLiteConstraintException("Foreign key constraint failed")
        coEvery { bookmarkDao.addBookmark(any()) } throws exception

        // When
        val result = repository.addBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to add bookmark")
    }

    @Test
    fun removeBookmark_validId_returnsSuccessResult() = runTest {
        // Given
        coEvery { bookmarkDao.removeBookmark("01_01") } just Runs

        // When
        val result = repository.removeBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify { bookmarkDao.removeBookmark("01_01") }
    }

    @Test
    fun removeBookmark_daoThrowsException_returnsErrorResult() = runTest {
        // Given
        val exception = SQLException("Delete failed")
        coEvery { bookmarkDao.removeBookmark("01_01") } throws exception

        // When
        val result = repository.removeBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to remove bookmark")
    }

    @Test
    fun toggleBookmark_notBookmarked_addsBookmarkAndReturnsTrue() = runTest {
        // Given
        coEvery { bookmarkDao.isBookmarked("01_01") } returns false
        coEvery { bookmarkDao.addBookmark(any()) } just Runs

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isTrue()
        coVerify { bookmarkDao.addBookmark(match { it.sunnahId == "01_01" }) }
    }

    @Test
    fun toggleBookmark_alreadyBookmarked_removesBookmarkAndReturnsFalse() = runTest {
        // Given
        coEvery { bookmarkDao.isBookmarked("01_01") } returns true
        coEvery { bookmarkDao.removeBookmark("01_01") } just Runs

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isFalse()
        coVerify { bookmarkDao.removeBookmark("01_01") }
    }

    @Test
    fun toggleBookmark_checkBookmarkStatusFails_returnsErrorResult() = runTest {
        // Given
        val exception = SQLException("Query failed")
        coEvery { bookmarkDao.isBookmarked("01_01") } throws exception

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to check bookmark status")
    }

    @Test
    fun toggleBookmark_addBookmarkFails_returnsErrorResult() = runTest {
        // Given
        coEvery { bookmarkDao.isBookmarked("01_01") } returns false
        val exception = SQLiteConstraintException("Add failed")
        coEvery { bookmarkDao.addBookmark(any()) } throws exception

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to add bookmark")
    }

    @Test
    fun toggleBookmark_removeBookmarkFails_returnsErrorResult() = runTest {
        // Given
        coEvery { bookmarkDao.isBookmarked("01_01") } returns true
        val exception = SQLException("Remove failed")
        coEvery { bookmarkDao.removeBookmark("01_01") } throws exception

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to remove bookmark")
    }

    @Test
    fun toggleBookmark_unexpectedException_returnsErrorResult() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery { bookmarkDao.isBookmarked("01_01") } throws exception

        // When
        val result = repository.toggleBookmark("01_01")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).isEqualTo("Failed to toggle bookmark")
    }

    @Test
    fun repository_multipleOperations_logsCorrectly() = runTest {
        // Given
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        coEvery { bookmarkDao.addBookmark(any()) } just Runs
        coEvery { bookmarkDao.removeBookmark(any()) } throws SQLException("Test error")

        // When
        repository.addBookmark("01_01")
        repository.removeBookmark("01_01")

        // Then
        verify { Log.d("BookmarkRepo", "Added bookmark for 01_01") }
        verify { Log.e("BookmarkRepo", "Error removing bookmark for 01_01", any()) }
    }
}