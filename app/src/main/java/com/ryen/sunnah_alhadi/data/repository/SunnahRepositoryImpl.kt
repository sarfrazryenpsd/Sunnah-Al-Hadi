package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SunnahRepositoryImpl @Inject constructor(
    val sunnahDao: SunnahDao,
    private val ioDispatcher: CoroutineDispatcher
) : SunnahRepository {

    @Volatile
    private var sunnahCountCache: Map<Int, Int>? = null
    private val cacheMutex = Mutex()

    override suspend fun getSunnahCounts(categoryIds: List<Int>): Map<Int, Int> {
        cacheMutex.withLock {
            sunnahCountCache?.let { return it }

            val counts = sunnahDao.getSunnahCountByCategoryIds(categoryIds.toSet())
            val cache = counts.associate { it.categoryId to it.sunnahCount }
            sunnahCountCache = cache
            return cache
        }
    }

    override suspend fun getAllSunnahs(): Result<List<Sunnah>> =
        withIoContext {
            try {
                val sunnahs = sunnahDao.getAllSunnahsWithBookmarkStatus()
                Result.Success(sunnahs.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e, "Failed to load sunnahs.")
            }
        }

    override suspend fun getSunnahById(id: String): Result<Sunnah?> =
        withIoContext {
            try {
                val result = sunnahDao.getSunnahByIdWithBookmarkStatus(id)
                Result.Success(result?.toDomain())
            } catch (e: Exception) {
                Result.Error(e, "Failed to load sunnah by id: $id")
            }
        }

    override suspend fun getSunnahsByIds(sunnahIds: List<String>): List<Sunnah> =
        withContext(Dispatchers.IO) {
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


    override suspend fun getSunnahsByCategory(categoryId: Int): Result<List<Sunnah>> =
        withIoContext {
            try {
                val sunnahs = sunnahDao.getSunnahsByCategoryWithBookmarkStatus(categoryId)
                Result.Success(sunnahs.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e, "Failed to load sunnahs by category: $categoryId")
            }
        }


    override suspend fun getRandomSunnahForSotd(excludeIds: List<String>): Result<Sunnah> =
        withIoContext {
            try {
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

    private suspend fun <T> withIoContext(block: suspend () -> T): T =
        withContext(ioDispatcher) { block() }
}
