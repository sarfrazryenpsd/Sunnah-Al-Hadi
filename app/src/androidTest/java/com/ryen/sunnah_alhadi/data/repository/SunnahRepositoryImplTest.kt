package com.ryen.sunnah_alhadi.data.repository

import android.database.SQLException
import android.database.sqlite.SQLiteException
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.Reference
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SunnahRepositoryImplTest {

    @MockK
    private lateinit var sunnahDao: SunnahDao

    private lateinit var sunnahRepository: SunnahRepository

    private val testSunnahWithBookmark = SunnahWithBookmark(
        sunnah = SunnahEntity(
            id = "01_01",
            categoryId = 1,
            title = "Test Sunnah",
            body = listOf(
                ContentBlock(
                    ContentType.ARABIC_TEXT,
                    ArabicSubtype.VERSE,
                    "Arabic text"
                )
            ),
            references = listOf(Reference("Bukhari")),
            extra = null
        ),
        isBookmarked = false,
        bookmarkedAt = null
    )

    private val testSunnahEntity = SunnahEntity(
        id = "01_02",
        categoryId = 1,
        title = "Test Sunnah Entity",
        body = listOf(ContentBlock(ContentType.ENGLISH_TEXT, EnglishSubtype.NORMAL, "English text")),
        references = null,
        extra = null
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sunnahRepository = SunnahRepositoryImpl(sunnahDao)
    }

    // region getAllSunnahs

    @Test
    fun getAllSunnahs_returns_success_when_dao_returns_data() = runTest {
        coEvery { sunnahDao.getAllSunnahsWithBookmarkStatus() } returns listOf(testSunnahWithBookmark)

        val result = sunnahRepository.getAllSunnahs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify { sunnahDao.getAllSunnahsWithBookmarkStatus() }
    }

    @Test
    fun getAllSunnahs_returns_empty_list_when_dao_returns_empty() = runTest {
        coEvery { sunnahDao.getAllSunnahsWithBookmarkStatus() } returns emptyList()

        val result = sunnahRepository.getAllSunnahs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data).isEmpty()
    }

    @Test
    fun getAllSunnahs_returns_error_when_dao_throws() = runTest {
        val exception = SQLiteException("Database error")
        coEvery { sunnahDao.getAllSunnahsWithBookmarkStatus() } throws exception

        val result = sunnahRepository.getAllSunnahs()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.exception).isEqualTo(exception)
        assertThat(error.message).isEqualTo("Failed to load sunnahs.")
    }

    // endregion

    // region getSunnahById

    @Test
    fun getSunnahById_returns_success_when_dao_returns_data() = runTest {
        val id = "01_01"
        coEvery { sunnahDao.getSunnahByIdWithBookmarkStatus(id) } returns testSunnahWithBookmark

        val result = sunnahRepository.getSunnahById(id)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data?.id).isEqualTo(id)
    }

    @Test
    fun getSunnahById_returns_success_with_null_when_dao_returns_null() = runTest {
        coEvery { sunnahDao.getSunnahByIdWithBookmarkStatus("unknown") } returns null

        val result = sunnahRepository.getSunnahById("unknown")

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val data = (result as Result.Success).data
        assertThat(data).isNull()
    }

    @Test
    fun getSunnahById_returns_error_when_dao_throws() = runTest {
        val id = "01_01"
        val exception = SQLiteException("Access denied")
        coEvery { sunnahDao.getSunnahByIdWithBookmarkStatus(id) } throws exception

        val result = sunnahRepository.getSunnahById(id)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.message).isEqualTo("Failed to load sunnah by id: $id")
    }

    // endregion

    // region getSunnahsByCategory

    @Test
    fun getSunnahsByCategory_returns_data() = runTest {
        coEvery { sunnahDao.getSunnahsByCategoryWithBookmarkStatus(1) } returns listOf(testSunnahWithBookmark)

        val result = sunnahRepository.getSunnahsByCategory(1)

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun getSunnahsByCategory_returns_error_on_failure() = runTest {
        val exception = IllegalStateException("Query failed")
        coEvery { sunnahDao.getSunnahsByCategoryWithBookmarkStatus(1) } throws exception

        val result = sunnahRepository.getSunnahsByCategory(1)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.message).isEqualTo("Failed to load sunnahs by category: 1")
    }

    // endregion

    // region getRandomSunnahs

    @Test
    fun getRandomSunnahs_returns_success() = runTest {
        coEvery { sunnahDao.getRandomSunnahsWithBookmarkStatus() } returns listOf(testSunnahWithBookmark)

        val result = sunnahRepository.getRandomSunnahs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun getRandomSunnahs_returns_error() = runTest {
        val exception = RuntimeException("Failure")
        coEvery { sunnahDao.getRandomSunnahsWithBookmarkStatus() } throws exception

        val result = sunnahRepository.getRandomSunnahs()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // endregion

    // region getBookmarkedSunnahs

    @Test
    fun getBookmarkedSunnahs_returns_data() = runTest {
        coEvery { sunnahDao.getBookmarkedSunnahs() } returns listOf(testSunnahWithBookmark)

        val result = sunnahRepository.getBookmarkedSunnahs()

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun getBookmarkedSunnahs_returns_error_on_exception() = runTest {
        val exception = SQLException("Error")
        coEvery { sunnahDao.getBookmarkedSunnahs() } throws exception

        val result = sunnahRepository.getBookmarkedSunnahs()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // endregion

    // region getRandomSunnahForSotd

    @Test
    fun getRandomSunnahForSotd_returns_success() = runTest {
        val exclude = listOf("01_01")
        coEvery { sunnahDao.getRandomSunnahIdForSotd(exclude) } returns "01_02"
        coEvery { sunnahDao.getSunnahById("01_02") } returns testSunnahEntity

        val result = sunnahRepository.getRandomSunnahForSotd(exclude)

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun getRandomSunnahForSotd_returns_error_if_not_found() = runTest {
        coEvery { sunnahDao.getRandomSunnahIdForSotd(any()) } returns "unknown"
        coEvery { sunnahDao.getSunnahById("unknown") } returns null

        val result = sunnahRepository.getRandomSunnahForSotd(emptyList())

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.message).isEqualTo("Sunnah not found")
    }

    @Test
    fun getRandomSunnahForSotd_handles_exception() = runTest {
        val exception = SQLException("Fail")
        coEvery { sunnahDao.getRandomSunnahIdForSotd(any()) } throws exception

        val result = sunnahRepository.getRandomSunnahForSotd(emptyList())

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // endregion

    // region getAllSunnahsWithBookmarkStatus

    @Test
    fun getAllSunnahsWithBookmarkStatus_returns_success() = runTest {
        coEvery { sunnahDao.getAllSunnahsWithBookmarkStatus() } returns listOf(testSunnahWithBookmark)

        val result = sunnahRepository.getAllSunnahsWithBookmarkStatus()

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun getAllSunnahsWithBookmarkStatus_returns_error() = runTest {
        val exception = RuntimeException("Failed to fetch")
        coEvery { sunnahDao.getAllSunnahsWithBookmarkStatus() } throws exception

        val result = sunnahRepository.getAllSunnahsWithBookmarkStatus()

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    // endregion
}
