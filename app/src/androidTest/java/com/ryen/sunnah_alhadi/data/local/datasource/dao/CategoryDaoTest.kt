package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao

    private val testCategories = listOf(
        CategoryEntity(id = 1, topic = "Prayer"),
        CategoryEntity(id = 2, topic = "Fasting"),
        CategoryEntity(id = 3, topic = "Charity"),
        CategoryEntity(id = 4, topic = "Hajj"),
        CategoryEntity(id = 5, topic = "Manners"),
        CategoryEntity(id = 6, topic = "Cleanliness"),
        CategoryEntity(id = 7, topic = "Family"),
        CategoryEntity(id = 8, topic = "Business")
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllCategories_empty_database_returns_empty_list() = runTest {
        // When
        val result = categoryDao.getAllCategories()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllCategories_returns_all_categories() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When
        val result = categoryDao.getAllCategories()

        // Then
        assertThat(result).hasSize(8)
        assertThat(result.map { it.topic }).containsExactlyElementsIn(
            testCategories.map { it.topic }
        )
    }

    @Test
    fun getCategoryById_existing_id_returns_category() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When
        val result = categoryDao.getCategoryById(3)

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(3)
        assertThat(result?.topic).isEqualTo("Charity")
    }

    @Test
    fun getCategoryById_non_existing_id_returns_null() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When
        val result = categoryDao.getCategoryById(999)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun getCategoryById_negative_id_returns_null() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When
        val result = categoryDao.getCategoryById(-1)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun getFeaturedCategories_returns_exactly_seven_categories() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When
        val result = categoryDao.getFeaturedCategories()

        // Then
        assertThat(result).hasSize(7)
        assertThat(result.map { it.id }).containsNoneIn(emptyList<Int>())
    }

    @Test
    fun getFeaturedCategories_less_than_seven_available_returns_all() = runTest {
        // Given
        val limitedCategories = testCategories.take(5)
        categoryDao.insertAll(limitedCategories)

        // When
        val result = categoryDao.getFeaturedCategories()

        // Then
        assertThat(result).hasSize(5)
        assertThat(result.map { it.id }).containsExactlyElementsIn(
            limitedCategories.map { it.id }
        )
    }

    @Test
    fun getFeaturedCategories_randomness_test() = runTest {
        // Given
        categoryDao.insertAll(testCategories)

        // When - Get featured categories multiple times
        val results = mutableListOf<List<CategoryEntity>>()
        repeat(10) {
            results.add(categoryDao.getFeaturedCategories())
        }

        // Then - At least one result should be different (high probability with 8 choose 7)
        val firstResult = results[0].map { it.id }.sorted()
        val hasDifferentResult = results.drop(1).any { result ->
            result.map { it.id }.sorted() != firstResult
        }

        // Note: There's a small chance all results are the same, but very unlikely
        assertThat(hasDifferentResult).isTrue()
    }

    @Test
    fun getFeaturedCategories_empty_database_returns_empty_list() = runTest {
        // When
        val result = categoryDao.getFeaturedCategories()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun insert_duplicate_id_replaces_existing() = runTest {
        // Given
        val originalCategory = CategoryEntity(id = 1, topic = "Original Topic")
        val updatedCategory = CategoryEntity(id = 1, topic = "Updated Topic")

        categoryDao.insertAll(listOf(originalCategory))

        // When
        categoryDao.insertAll(listOf(updatedCategory))

        // Then
        val result = categoryDao.getCategoryById(1)
        assertThat(result?.topic).isEqualTo("Updated Topic")

        val allCategories = categoryDao.getAllCategories()
        assertThat(allCategories).hasSize(1)
    }

    @Test
    fun insert_categories_with_special_characters_in_topic() = runTest {
        // Given
        val specialCategories = listOf(
            CategoryEntity(id = 1, topic = "Prayer & Worship"),
            CategoryEntity(id = 2, topic = "Fasting (Ramadan)"),
            CategoryEntity(id = 3, topic = "Charity/Zakat"),
            CategoryEntity(id = 4, topic = "Hajj & Umrah"),
            CategoryEntity(id = 5, topic = "Prophet's Manners ﷺ")
        )

        // When
        categoryDao.insertAll(specialCategories)

        // Then
        val result = categoryDao.getAllCategories()
        assertThat(result).hasSize(5)
        assertThat(result.map { it.topic }).containsExactlyElementsIn(
            specialCategories.map { it.topic }
        )
    }

    @Test
    fun insert_categories_with_empty_topic_stores_successfully() = runTest {
        // Given
        val categoryWithEmptyTopic = CategoryEntity(id = 1, topic = "")

        // When
        categoryDao.insertAll(listOf(categoryWithEmptyTopic))

        // Then
        val result = categoryDao.getCategoryById(1)
        assertThat(result).isNotNull()
        assertThat(result?.topic).isEqualTo("")
    }

    @Test
    fun insert_large_number_of_categories_handles_correctly() = runTest {
        // Given
        val largeList = (1..1000).map {
            CategoryEntity(id = it, topic = "Category $it")
        }

        // When
        categoryDao.insertAll(largeList)

        // Then
        val result = categoryDao.getAllCategories()
        assertThat(result).hasSize(1000)

        // Verify some random entries
        val category500 = categoryDao.getCategoryById(500)
        assertThat(category500?.topic).isEqualTo("Category 500")
    }

    @Test
    fun concurrent_access_maintains_data_integrity() = runTest {
        // Given
        val categories1 = (1..50).map { CategoryEntity(id = it, topic = "Category $it") }
        val categories2 = (51..100).map { CategoryEntity(id = it, topic = "Category $it") }

        // When - Simulate concurrent insertions
        val job1 = async { categoryDao.insertAll(categories1) }
        val job2 = async { categoryDao.insertAll(categories2) }

        job1.await()
        job2.await()

        // Then
        val result = categoryDao.getAllCategories()
        assertThat(result).hasSize(100)
    }

    @Test
    fun database_query_performance_with_large_dataset() = runTest {
        // Given
        val largeList = (1..5000).map {
            CategoryEntity(id = it, topic = "Category $it")
        }
        categoryDao.insertAll(largeList)

        // When & Then - Measure performance
        val startTime = System.currentTimeMillis()

        val allCategories = categoryDao.getAllCategories()
        val specificCategory = categoryDao.getCategoryById(2500)
        val featuredCategories = categoryDao.getFeaturedCategories()
        val endTime = System.currentTimeMillis()

        // Verify correctness
        assertThat(allCategories).hasSize(5000)
        assertThat(specificCategory?.topic).isEqualTo("Category 2500")
        assertThat(featuredCategories).hasSize(7)

        // Performance assertion (should complete within reasonable time)
        val totalTime = endTime - startTime
        assertThat(totalTime).isLessThan(1000) // Should complete within 1 second
    }

    @Test
    fun getFeaturedCategories_consistent_behavior_with_exact_seven_categories() = runTest {
        // Given - Exactly 7 categories
        val exactSevenCategories = testCategories.take(7)
        categoryDao.insertAll(exactSevenCategories)

        // When
        val result = categoryDao.getFeaturedCategories()

        // Then
        assertThat(result).hasSize(7)
        assertThat(result.map { it.id }).containsExactlyElementsIn(
            exactSevenCategories.map { it.id }
        )
    }
}