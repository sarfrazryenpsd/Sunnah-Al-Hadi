package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Sunnah

interface SunnahRepository {

    suspend fun getAllSunnahs(): Result<List<Sunnah>>
    suspend fun getSunnahById(id: String): Result<Sunnah?>
    suspend fun getSunnahsByCategory(categoryId: Int): Result<List<Sunnah>>
    suspend fun getRandomSunnahs(): Result<List<Sunnah>>
    suspend fun searchSunnahs(query: String): Result<List<Sunnah>>
    suspend fun getBookmarkedSunnahs(): Result<List<Sunnah>>
    suspend fun getAllSunnahsWithBookmarkStatus(): Result<List<Sunnah>>
    suspend fun getRandomSunnahForSotd(excludeIds: List<String>): Result<Sunnah>
}