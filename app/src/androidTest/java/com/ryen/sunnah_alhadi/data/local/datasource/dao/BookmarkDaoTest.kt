package com.ryen.sunnah_alhadi.data.local.datasource.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
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
        categoryDao.insertCategory(testCategory)
        sunnahDao.insertSunnah(testSunnah)
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
}