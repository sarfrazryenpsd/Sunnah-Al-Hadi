package com.ryen.sunnah_alhadi.data.local.datasource.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.Reference
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SunnahDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var sunnahDao: SunnahDao
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var categoryDao: CategoryDao

    private val testCategory = CategoryEntity(id = 1, topic = "Test Category")
    private val testSunnah = SunnahEntity(
        id = "01_01",
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
        extra = null
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        sunnahDao = database.sunnahDao()
        bookmarkDao = database.bookmarkDao()
        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllSunnahs_returns_all_sunnahs() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = listOf(
            testSunnah,
            testSunnah.copy(id = "01_02", title = "Second Sunnah")
        )
        database.sunnahDao().insertAll(sunnahs)

        // When
        val result = sunnahDao.getAllSunnahs()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactly("Test Sunnah", "Second Sunnah")
    }

    @Test
    fun getSunnahById_existing_id_returns_sunnah() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))

        // When
        val result = sunnahDao.getSunnahById("01_01")

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo("01_01")
        assertThat(result?.title).isEqualTo("Test Sunnah")
    }

    @Test
    fun getSunnahById_non_existing_id_returns_null() = runTest {
        // When
        val result = sunnahDao.getSunnahById("99_99")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun getAllSunnahsWithBookmarkStatus_without_bookmarks_returns_all_unmarked() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))

        // When
        val result = sunnahDao.getAllSunnahsWithBookmarkStatus()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].isBookmarked).isFalse()
        assertThat(result[0].bookmarkedAt).isNull()
    }

    @Test
    fun getAllSunnahsWithBookmarkStatus_with_bookmarks_returns_correct_status() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))
        val bookmark = BookmarkEntity(sunnahId = "01_01", bookmarkedAt = System.currentTimeMillis())
        bookmarkDao.addBookmark(bookmark)

        // When
        val result = sunnahDao.getAllSunnahsWithBookmarkStatus()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].isBookmarked).isTrue()
        assertThat(result[0].bookmarkedAt).isEqualTo(bookmark.bookmarkedAt)
    }

    @Test
    fun getBookmarkedSunnahs_returns_only_bookmarked_sunnahs() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = listOf(
            testSunnah,
            testSunnah.copy(id = "01_02", title = "Unbookmarked Sunnah")
        )
        database.sunnahDao().insertAll(sunnahs)
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))

        // When
        val result = sunnahDao.getBookmarkedSunnahs()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].isBookmarked).isTrue()
        assertThat(result[0].sunnah.id).isEqualTo("01_01")
    }

    @Test
    fun getBookmarkedSunnahsFlow_emits_bookmarked_sunnahs() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))

        // When
        val flow = sunnahDao.getBookmarkedSunnahsFlow()

        // Initially no bookmarks
        var result = flow.first()
        assertThat(result).isEmpty()

        // Add bookmark
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))

        // Then
        result = flow.first()
        assertThat(result).hasSize(1)
        assertThat(result[0].isBookmarked).isTrue()
    }

    @Test
    fun getSunnahsByCategory_returns_sunnahs_for_category() = runTest {
        // Given
        val category1 = CategoryEntity(id = 1, topic = "Category 1")
        val category2 = CategoryEntity(id = 2, topic = "Category 2")
        categoryDao.insertAll(listOf(category1, category2))

        val sunnahs = listOf(
            testSunnah.copy(id = "01_01", categoryId = 1),
            testSunnah.copy(id = "01_02", categoryId = 1),
            testSunnah.copy(id = "02_01", categoryId = 2)
        )
        database.sunnahDao().insertAll(sunnahs)

        // When
        val result = sunnahDao.getSunnahsByCategoryWithBookmarkStatus(1)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.sunnah.categoryId }).containsExactlyElementsIn(listOf(1, 1))
    }

    @Test
    fun getSunnahsByCategoryFlow_emits_category_sunnahs() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))

        // When
        val flow = sunnahDao.getSunnahsByCategoryWithBookmarkStatusFlow(1)
        val result = flow.first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].sunnah.categoryId).isEqualTo(1)
    }

    @Test
    fun getSunnahByIdWithBookmarkStatus_returns_sunnah_with_bookmark_info() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))
        val bookmark = BookmarkEntity(sunnahId = "01_01", bookmarkedAt = 123456789L)
        bookmarkDao.addBookmark(bookmark)

        // When
        val result = sunnahDao.getSunnahByIdWithBookmarkStatus("01_01")

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.isBookmarked).isTrue()
        assertThat(result?.bookmarkedAt).isEqualTo(123456789L)
    }

    @Test
    fun getRandomSunnahsWithBookmarkStatus_returns_sunnahs_in_random_order() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = (1..10).map {
            testSunnah.copy(id = "01_${it.toString().padStart(2, '0')}", title = "Sunnah $it")
        }
        database.sunnahDao().insertAll(sunnahs)

        // When - Run multiple times to check randomness
        val results = mutableListOf<List<SunnahWithBookmark>>()
        repeat(5) {
            results.add(sunnahDao.getRandomSunnahsWithBookmarkStatus())
        }

        // Then
        for (result in results) {
            assertThat(result).hasSize(10)
        }

        // Check that at least one result is in different order (high probability with 10 items)
        val firstOrder = results[0].map { it.sunnah.id }
        val hasDifferentOrder = results.drop(1).any { result ->
            result.map { it.sunnah.id } != firstOrder
        }
        assertThat(hasDifferentOrder).isTrue()
    }

    @Test
    fun getRandomSunnahIdForSotd_excludes_specified_ids() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = (1..5).map {
            testSunnah.copy(id = "01_${it.toString().padStart(2, '0')}")
        }
        database.sunnahDao().insertAll(sunnahs)

        val excludeIds = listOf("01_01", "01_02", "01_03")

        // When
        val result = sunnahDao.getRandomSunnahIdForSotd(excludeIds)

        // Then
        assertThat(result).isNotIn(excludeIds)
        assertThat(result).isIn(listOf("01_04", "01_05"))
    }

    @Test
    fun getRandomSunnahIdForSotd_empty_exclude_list_returns_any_id() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))

        // When
        val result = sunnahDao.getRandomSunnahIdForSotd(emptyList())

        // Then
        assertThat(result).isEqualTo("01_01")
    }

    @Test
    fun foreign_key_constraint_prevents_orphaned_sunnahs() = runTest {
        // Given - No category exists
        val orphanedSunnah = testSunnah.copy(categoryId = 999)

        // When & Then
        try {
            sunnahDao.insertAll(listOf(orphanedSunnah))
            fail("Expected SQLiteConstraintException to be thrown")
        } catch (e: SQLiteConstraintException) {
            // Expected
        }
    }

    @Test
    fun cascade_delete_removes_bookmarks_when_sunnah_deleted() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        database.sunnahDao().insertAll(listOf(testSunnah))
        bookmarkDao.addBookmark(BookmarkEntity(sunnahId = "01_01"))

        // Verify bookmark exists
        assertThat(bookmarkDao.isBookmarked("01_01")).isTrue()

        // When - Delete sunnah (simulating CASCADE)
        database.sunnahDao().delete(testSunnah)

        // Then - Bookmark should be removed due to CASCADE
        assertThat(bookmarkDao.isBookmarked("01_01")).isFalse()
    }

    @Test
    fun complex_query_with_sorting_returns_correct_order() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = listOf(
            testSunnah.copy(id = "01_01", title = "Zebra Sunnah"),
            testSunnah.copy(id = "01_02", title = "Alpha Sunnah"),
            testSunnah.copy(id = "01_03", title = "Beta Sunnah")
        )
        database.sunnahDao().insertAll(sunnahs)

        // When
        val result = sunnahDao.getAllSunnahsWithBookmarkStatus()

        // Then - Should be sorted by title ASC
        assertThat(result.map { it.sunnah.title }).containsExactly(
            "Alpha Sunnah", "Beta Sunnah", "Zebra Sunnah"
        ).inOrder()
    }

    @Test
    fun database_transaction_rollback_on_error() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val validSunnah = testSunnah.copy(id = "01_01")
        val invalidSunnah = testSunnah.copy(id = "01_02", categoryId = 999) // Invalid category

        // When & Then
        try {
            database.withTransaction {
                sunnahDao.insertAll(listOf(validSunnah))
                sunnahDao.insertAll(listOf(invalidSunnah))
            }
            fail("Expected SQLiteConstraintException to be thrown")
        } catch (e: SQLiteConstraintException) {
            // Expected
        }

        // Verify rollback
        val allSunnahs = sunnahDao.getAllSunnahs()
        assertThat(allSunnahs).isEmpty()

    }

    @Test
    fun concurrent_access_maintains_data_integrity() = runTest {
        // Given
        categoryDao.insertAll(listOf(testCategory))
        val sunnahs = (1..100).map {
            testSunnah.copy(id = "01_${it.toString().padStart(2, '0')}", title = "Sunnah $it")
        }

        // When - Simulate concurrent insertions
        val jobs = sunnahs.map { sunnah ->
            async {
                database.sunnahDao().insertAll(listOf(sunnah))
            }
        }
        jobs.awaitAll()

        // Then
        val result = sunnahDao.getAllSunnahs()
        assertThat(result).hasSize(100)
    }
}