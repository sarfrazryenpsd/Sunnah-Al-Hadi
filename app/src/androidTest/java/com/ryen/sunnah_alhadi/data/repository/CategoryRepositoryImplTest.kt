package com.ryen.sunnah_alhadi.data.repository

import android.database.sqlite.SQLiteException
import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryImplTest {

    @MockK
    private lateinit var categoryDao: CategoryDao

    private lateinit var categoryRepository: CategoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        categoryRepository = CategoryRepositoryImpl(categoryDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getAllCategories_returnsMappedList_whenDaoReturnsData() = runTest {
        val entities = listOf(
            CategoryEntity(1, "Prayer"),
            CategoryEntity(2, "Charity")
        )
        coEvery { categoryDao.getAllCategories() } returns entities

        val result = categoryRepository.getAllCategories()

        assertThat(result).hasSize(2)
        assertThat(result[0].topic).isEqualTo("Prayer")
        assertThat(result[1].topic).isEqualTo("Charity")
        coVerify(exactly = 1) { categoryDao.getAllCategories() }
    }

    @Test
    fun getAllCategories_returnsEmpty_whenDaoReturnsEmptyList() = runTest {
        coEvery { categoryDao.getAllCategories() } returns emptyList()

        val result = categoryRepository.getAllCategories()

        assertThat(result).isEmpty()
        coVerify(exactly = 1) { categoryDao.getAllCategories() }
    }

    @Test
    fun getAllCategories_throwsException_whenDaoFails() = runTest {
        val exception = SQLiteException("DB error")
        coEvery { categoryDao.getAllCategories() } throws exception

        try {
            categoryRepository.getAllCategories()
            fail("Expected SQLiteException was not thrown")
        } catch (e: SQLiteException) {
            assertThat(e.message).isEqualTo("DB error")
        }

        coVerify { categoryDao.getAllCategories() }
    }


    @Test
    fun getCategoryById_returnsMapped_whenDaoReturnsEntity() = runTest {
        val entity = CategoryEntity(1, "Prayer")
        coEvery { categoryDao.getCategoryById(1) } returns entity

        val result = categoryRepository.getCategoryById(1)

        assertThat(result).isNotNull()
        assertThat(result?.topic).isEqualTo("Prayer")
        coVerify { categoryDao.getCategoryById(1) }
    }

    @Test
    fun getCategoryById_returnsNull_whenDaoReturnsNull() = runTest {
        coEvery { categoryDao.getCategoryById(999) } returns null

        val result = categoryRepository.getCategoryById(999)

        assertThat(result).isNull()
        coVerify { categoryDao.getCategoryById(999) }
    }

    @Test
    fun getCategoryById_throwsException_whenDaoFails() = runTest {
        val exception = SQLiteException("DB failure")
        coEvery { categoryDao.getCategoryById(1) } throws exception

        try {
            categoryRepository.getCategoryById(1)
            fail("Expected SQLiteException was not thrown")
        } catch (e: SQLiteException) {
            assertThat(e.message).isEqualTo("DB failure")
        }

        coVerify { categoryDao.getCategoryById(1) }
    }

    @Test
    fun getFeaturedCategories_returnsMapped_whenDaoReturnsData() = runTest {
        val entities = listOf(
            CategoryEntity(1, "Prayer"),
            CategoryEntity(2, "Fasting")
        )
        coEvery { categoryDao.getFeaturedCategories() } returns entities

        val result = categoryRepository.getFeaturedCategories()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.topic }).containsExactly("Prayer", "Fasting")
        coVerify { categoryDao.getFeaturedCategories() }
    }

    @Test
    fun getFeaturedCategories_returnsEmpty_whenDaoReturnsEmpty() = runTest {
        coEvery { categoryDao.getFeaturedCategories() } returns emptyList()

        val result = categoryRepository.getFeaturedCategories()

        assertThat(result).isEmpty()
        coVerify { categoryDao.getFeaturedCategories() }
    }

    @Test
    fun getFeaturedCategories_throwsException_whenDaoFails() = runTest {
        val exception = IllegalStateException("Random failure")
        coEvery { categoryDao.getFeaturedCategories() } throws exception

        try {
            categoryRepository.getFeaturedCategories()
            fail("Expected IllegalStateException was not thrown")
        } catch (e: IllegalStateException) {
            assertThat(e.message).isEqualTo("Random failure")
        }

        coVerify { categoryDao.getFeaturedCategories() }
    }

}
