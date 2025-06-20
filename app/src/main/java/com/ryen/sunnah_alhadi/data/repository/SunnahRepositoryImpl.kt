package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class SunnahRepositoryImpl(
    private val sunnahDao: SunnahDao,
    private val bookmarkDao: BookmarkDao
) : SunnahRepository {

    override suspend fun getAllSunnahs(): RepositoryResult<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getAllSunnahsWithBookmarkStatus()
        RepositoryResult.Success(sunnahs.map { it.toDomain() })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load sunnahs.")
    }

    override suspend fun getSunnahById(id: String): RepositoryResult<Sunnah?> = try {
        val result = sunnahDao.getSunnahByIdWithBookmarkStatus(id)
        RepositoryResult.Success(result?.toDomain())
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load sunnah by id: $id")
    }

    override suspend fun getSunnahsByCategory(categoryId: Int): RepositoryResult<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getSunnahsByCategoryWithBookmarkStatus(categoryId)
        RepositoryResult.Success(sunnahs.map { it.toDomain() })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load sunnahs by category: $categoryId")
    }

    override suspend fun getRandomSunnahs(): RepositoryResult<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getRandomSunnahs()
        RepositoryResult.Success(sunnahs.map {
            val isBookmarked = bookmarkDao.isBookmarked(it.id)
            it.toDomain(isBookmarked)
        })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load random sunnahs.")
    }

    override suspend fun searchSunnahs(query: String): RepositoryResult<List<Sunnah>> = try {
        val allSunnahs = sunnahDao.getAllSunnahsWithBookmarkStatus()
        val filtered = allSunnahs.filter {
            it.sunnah.title.contains(query, ignoreCase = true) ||
                    it.sunnah.body.any { block -> block.content.contains(query, ignoreCase = true) }
        }
        RepositoryResult.Success(filtered.map { it.toDomain() })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to search sunnahs with query: $query")
    }

    override suspend fun getBookmarkedSunnahs(): RepositoryResult<List<Sunnah>> = try {
        val bookmarked = sunnahDao.getBookmarkedSunnahs()
        RepositoryResult.Success(bookmarked.map { it.toDomain() })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load bookmarked sunnahs.")
    }

    override suspend fun getAllSunnahsWithBookmarkStatus(): RepositoryResult<List<Sunnah>> = try {
        val all = sunnahDao.getAllSunnahsWithBookmarkStatus()
        RepositoryResult.Success(all.map { it.toDomain() })
    } catch (e: Exception) {
        RepositoryResult.Error(e, "Failed to load all sunnahs with bookmark status.")
    }
}
