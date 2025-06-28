package com.ryen.sunnah_alhadi.data.local.datasource.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.google.common.truth.Truth.assertThat
import app.cash.turbine.test
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class BookmarkDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var sunnahDao: SunnahDao
    private lateinit var categoryDao: CategoryDao

    // Test data
    private val testCategory = CategoryEntity(id = 1, topic = "Test Category")
    private val testSunnah = SunnahEntity(
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
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        bookmarkDao = database.bookmarkDao()
        sunnahDao = database.sunnahDao()
        categoryDao = database.categoryDao()

    }

    @After
    fun closeDb() {
        database.close()
    }

    private suspend fun setupTestData() {
        // Insert test category and sunnah first
        val testSunnahDao = database.sunnahDao() as TestSunnahDao
        val testCategoryDao = database.sunnahDao() as TestCategoryDao
        testCategoryDao.insertCategory(testCategory)
        testSunnahDao.insertSunnah(testSunnah)
    }

    @Test
    fun addBookmark_validSunnahId_bookmarkAdded() = runTest {
        // Given
        setupTestData()
        val bookmark = BookmarkEntity(sunnahId = "01_01")

        // When
        bookmarkDao.addBookmark(bookmark)

        // Then
        val isBookmarked = bookmarkDao.isBookmarked("01_01")
        assertThat(isBookmarked).isTrue()
    }

    @Test
    fun addBookmark_duplicateSunnah_replacesExisting() = runTest {
        // Given
        setupTestData()
        val originalTime = System.currentTimeMillis() - 1000L
        val newTime = System.currentTimeMillis()

        val originalBookmark = BookmarkEntity(sunnahId = "01_01", bookmarkedAt = originalTime)
        val newBookmark = BookmarkEntity(sunnahId = "01_01", bookmarkedAt = newTime)

        // When
        bookmarkDao.addBookmark(originalBookmark)
        bookmarkDao.addBookmark(newBookmark)

        // Then
        val allBookmarks = bookmarkDao.getAllBookmarks()
        assertThat(allBookmarks).hasSize(1)
        assertThat(allBookmarks[0].bookmarkedAt).isEqualTo(newTime)
    }

    @Test
    fun removeBookmark_existingBookmark_bookmarkRemoved() = runTest {
        // Given
        setupTestData()
        val bookmark = BookmarkEntity(sunnahId = "01_01")
        bookmarkDao.addBookmark(bookmark)

        // When
        bookmarkDao.removeBookmark("01_01")

        // Then
        val isBookmarked = bookmarkDao.isBookmarked("01_01")
        assertThat(isBookmarked).isFalse()
    }

    @Test
    fun removeBookmark_nonExistentBookmark_doesNotCrash() = runTest {
        // Given
        setupTestData()

        // When & Then - Should not throw exception
        bookmarkDao.removeBookmark("99_99")

        // Verify no bookmarks exist
        val allBookmarks = bookmarkDao.getAllBookmarks()
        assertThat(allBookmarks).isEmpty()
    }

    @Test
    fun toggleBookmark_notBookmarked_addsBookmark() = runTest {
        // Given
        setupTestData()

        // When
        val result = bookmarkDao.toggleBookmark("01_01")

        // Then
        assertThat(result).isTrue()
        assertThat(bookmarkDao.isBookmarked("01_01")).isTrue()
    }

    @Test
    fun toggleBookmark_alreadyBookmarked_removesBookmark() = runTest {
        // Given
        setupTestData()
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))

        // When
        val result = bookmarkDao.toggleBookmark("01_01")

        // Then
        assertThat(result).isFalse()
        assertThat(bookmarkDao.isBookmarked("01_01")).isFalse()
    }

    @Test
    fun getAllBookmarks_multipleBookmarks_returnsOrderedByTime() = runTest {
        // Given
        setupTestData()
        val testSunnahDao = database.sunnahDao() as TestSunnahDao
        val sunnah2 = testSunnah.copy(id = "01_02")
        val sunnah3 = testSunnah.copy(id = "01_03")
        testSunnahDao.insertSunnah(sunnah2)
        testSunnahDao.insertSunnah(sunnah3)

        val time1 = System.currentTimeMillis() - 2000L
        val time2 = System.currentTimeMillis() - 1000L
        val time3 = System.currentTimeMillis()

        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01", bookmarkedAt = time1))
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_02", bookmarkedAt = time2))
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_03", bookmarkedAt = time3))

        // When
        val bookmarks = bookmarkDao.getAllBookmarks()

        // Then
        assertThat(bookmarks).hasSize(3)
        assertThat(bookmarks[0].sunnahId).isEqualTo("01_03") // Most recent first
        assertThat(bookmarks[1].sunnahId).isEqualTo("01_02")
        assertThat(bookmarks[2].sunnahId).isEqualTo("01_01")
    }

    @Test
    fun getBookmarkedSunnahs_withBookmarks_returnsSunnahsOrderedByBookmarkTime() = runTest {
        // Given
        setupTestData()
        val testSunnahDao = database.sunnahDao() as TestSunnahDao
        val sunnah2 = testSunnah.copy(id = "01_02", title = "Second Sunnah")
        testSunnahDao.insertSunnah(sunnah2)

        val time1 = System.currentTimeMillis() - 1000L
        val time2 = System.currentTimeMillis()

        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01", bookmarkedAt = time1))
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_02", bookmarkedAt = time2))

        // When
        val bookmarkedSunnahs = bookmarkDao.getBookmarkedSunnahs()

        // Then
        assertThat(bookmarkedSunnahs).hasSize(2)
        assertThat(bookmarkedSunnahs[0].id).isEqualTo("01_02") // Most recent first
        assertThat(bookmarkedSunnahs[1].id).isEqualTo("01_01")
    }

    @Test
    fun getBookmarkedSunnahsFlow_emitsUpdatesOnBookmarkChange() = runTest {
        // Given
        setupTestData()

        // When & Then
        bookmarkDao.getBookmarkedSunnahsFlow().test {
            // Initially empty
            assertThat(awaitItem()).isEmpty()

            // Add bookmark
            bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))
            val firstEmission = awaitItem()
            assertThat(firstEmission).hasSize(1)
            assertThat(firstEmission[0].id).isEqualTo("01_01")

            // Remove bookmark
            bookmarkDao.removeBookmark("01_01")
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun isBookmarked_existingBookmark_returnsTrue() = runTest {
        // Given
        setupTestData()
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))

        // When
        val result = bookmarkDao.isBookmarked("01_01")

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun isBookmarked_nonExistentBookmark_returnsFalse() = runTest {
        // Given
        setupTestData()

        // When
        val result = bookmarkDao.isBookmarked("01_01")

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun databaseConstraints_foreignKeyViolation_throwsException() = runTest {
        // Given - No setup, trying to bookmark non-existent sunnah

        // When & Then
        try {
            bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "99_99"))
            fail("Expected SQLiteConstraintException was not thrown")
        } catch (e: SQLiteConstraintException) {
            // Test passes
        }
    }

    @Test
    fun bookmarkOperations_concurrentAccess_handledCorrectly() = runTest {
        // Given
        setupTestData()

        // When - Simulate concurrent bookmark operations
        val jobs = (1..10).map { index ->
            async {
                if (index % 2 == 0) {
                    bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))
                } else {
                    bookmarkDao.removeBookmark("01_01")
                }
            }
        }
        jobs.awaitAll()

        // Then - Should not crash and have consistent state
        val isBookmarked = bookmarkDao.isBookmarked("01_01")
        val allBookmarks = bookmarkDao.getAllBookmarks()

        if (isBookmarked) {
            assertThat(allBookmarks).hasSize(1)
        } else {
            assertThat(allBookmarks).isEmpty()
        }
    }
}