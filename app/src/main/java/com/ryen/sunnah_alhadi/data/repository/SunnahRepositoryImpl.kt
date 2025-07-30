package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SunnahRepositoryImpl @Inject constructor(
    val sunnahDao: SunnahDao,
) : SunnahRepository {

    private var sunnahCountCache: Map<Int, Int>? = null

    override suspend fun getSunnahCounts(categoryIds: List<Int>): Map<Int, Int> {
        if (sunnahCountCache != null) {
            return sunnahCountCache!!
        }

        val counts = sunnahDao.getSunnahCountByCategoryIds(categoryIds.toSet())
        sunnahCountCache = counts.associate { it.categoryId to it.sunnahCount }
        return sunnahCountCache!!
    }

    override suspend fun getAllSunnahs(): Result<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getAllSunnahsWithBookmarkStatus()
        Result.Success(sunnahs.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, "Failed to load sunnahs.")
    }

    override suspend fun getSunnahById(id: String): Result<Sunnah?> = try {
        val result = sunnahDao.getSunnahByIdWithBookmarkStatus(id)
        Result.Success(result?.toDomain())
    } catch (e: Exception) {
        Result.Error(e, "Failed to load sunnah by id: $id")
    }

    override suspend fun getSunnahsByIds(sunnahIds: List<String>): List<Sunnah> {
        return withContext(Dispatchers.IO) {
            try {
                if (sunnahIds.isEmpty()) {
                    return@withContext emptyList()
                }

                val sunnahEntities = sunnahDao.getSunnahsByIds(sunnahIds)

                // Map entities to domain models and preserve the order of input list
                val sunnahMap = sunnahEntities.associateBy { it.id }
                sunnahIds.mapNotNull { id ->
                    sunnahMap[id]?.toDomain()
                }
            } catch (e: Exception) {
                // Log error in production
                emptyList()
            }
        }
    }

    override suspend fun getSunnahsByCategory(categoryId: Int): Result<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getSunnahsByCategoryWithBookmarkStatus(categoryId)
        Result.Success(sunnahs.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, "Failed to load sunnahs by category: $categoryId")
    }

    override suspend fun getRandomSunnahs(): Result<List<Sunnah>> = try {
        val sunnahs = sunnahDao.getRandomSunnahsWithBookmarkStatus()
        Result.Success(sunnahs.map {
            it.toDomain()
        })
    } catch (e: Exception) {
        Result.Error(e, "Failed to load random sunnahs.")
    }

    override suspend fun searchSunnahs(query: String): Result<List<Sunnah>> = try {
        val allSunnahs = sunnahDao.getAllSunnahsWithBookmarkStatus()
        val filtered = allSunnahs.filter {
            it.sunnah.title.contains(query, ignoreCase = true) ||
                    it.sunnah.body.any { block -> block.content.contains(query, ignoreCase = true) }
        }
        Result.Success(filtered.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, "Failed to search sunnahs with query: $query")
    }

    override suspend fun getBookmarkedSunnahs(): Result<List<Sunnah>> = try {
        val bookmarked = sunnahDao.getBookmarkedSunnahs()
        Result.Success(bookmarked.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, "Failed to load bookmarked sunnahs.")
    }

    override suspend fun getAllSunnahsWithBookmarkStatus(): Result<List<Sunnah>> = try {
        val all = sunnahDao.getAllSunnahsWithBookmarkStatus()
        Result.Success(all.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, "Failed to load all sunnahs with bookmark status.")
    }

    override suspend fun getRandomSunnahForSotd(excludeIds: List<String>): Result<Sunnah> = try {
        val sunnahId = sunnahDao.getRandomSunnahIdForSotd(excludeIds)
        val sunnahEntity = sunnahDao.getSunnahById(sunnahId)
        if (sunnahEntity != null) {
            Result.Success(sunnahEntity.toDomain())
        } else {
            Result.Error(Exception("Sunnah not found"), "Sunnah not found")
        }
    } catch (e: Exception) {
        Result.Error(e, "Failed to load random sunnah id for SOTD")
    }
}
