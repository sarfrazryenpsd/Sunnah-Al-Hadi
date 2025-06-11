package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class SunnahRepositoryImpl (
    private val sunnahDao: SunnahDao,
    private val bookmarkDao: BookmarkDao
) : SunnahRepository {

    override suspend fun getAllSunnahs(): List<Sunnah> {
        val sunnahs = sunnahDao.getAllSunnahs()
        return sunnahs.map { sunnah ->
            val isBookmarked = bookmarkDao.isBookmarked(sunnah.id)
            sunnah.toDomain(isBookmarked = isBookmarked)
        }
    }

    override suspend fun getSunnahById(id: String): Sunnah? {
        val sunnah = sunnahDao.getSunnahById(id) ?: return null
        val isBookmarked = bookmarkDao.isBookmarked(id)
        return sunnah.toDomain(isBookmarked = isBookmarked)
    }

    override suspend fun getSunnahsByCategory(categoryId: Int): List<Sunnah> {
        val sunnahs = sunnahDao.getSunnahsByCategory(categoryId)
        return sunnahs.map { sunnah ->
            val isBookmarked = bookmarkDao.isBookmarked(sunnah.id)
            sunnah.toDomain(isBookmarked = isBookmarked)
        }
    }

    override suspend fun getRandomSunnahs(): List<Sunnah> {
        val sunnahs = sunnahDao.getRandomSunnahs()
        return sunnahs.map { sunnah ->
            val isBookmarked = bookmarkDao.isBookmarked(sunnah.id)
            sunnah.toDomain(isBookmarked = isBookmarked)
        }
    }

    override suspend fun searchSunnahs(query: String): List<Sunnah> {
        // Since you don't have a search query in DAO, I'll implement a basic search
        // You may want to add a proper FTS search query to your DAO
        val allSunnahs = sunnahDao.getAllSunnahs()
        val filteredSunnahs = allSunnahs.filter { sunnah ->
            sunnah.title.contains(query, ignoreCase = true) ||
                    sunnah.body.any { contentBlock ->
                        contentBlock.content.contains(query, ignoreCase = true)
                    }
        }

        return filteredSunnahs.map { sunnah ->
            val isBookmarked = bookmarkDao.isBookmarked(sunnah.id)
            sunnah.toDomain(isBookmarked = isBookmarked)
        }
    }

    override suspend fun updateLastSeen(sunnahId: String, timestamp: Long) {
        // Since you don't have lastSeen in your entity, this would require
        // adding a lastSeen field to SunnahEntity or creating a separate table
        // For now, I'll leave this as a placeholder
        // TODO: Implement based on your preferred approach
    }
}