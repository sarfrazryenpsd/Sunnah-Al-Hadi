package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah

interface SunnahRepository {

    suspend fun getAllSunnahs(): RepositoryResult<List<Sunnah>>
    suspend fun getSunnahById(id: String): RepositoryResult<Sunnah?>
    suspend fun getSunnahsByCategory(categoryId: Int): RepositoryResult<List<Sunnah>>
    suspend fun getRandomSunnahs(): RepositoryResult<List<Sunnah>>
    suspend fun searchSunnahs(query: String): RepositoryResult<List<Sunnah>>
    suspend fun getBookmarkedSunnahs(): RepositoryResult<List<Sunnah>>
    suspend fun getAllSunnahsWithBookmarkStatus(): RepositoryResult<List<Sunnah>>
}