package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.domain.model.Sunnah

interface SunnahRepository {
    suspend fun getAllSunnahs(): List<Sunnah>
    suspend fun getSunnahById(id: String): Sunnah?
    suspend fun getSunnahsByCategory(categoryId: Int): List<Sunnah>
    suspend fun getRandomSunnahs(): List<Sunnah>
    suspend fun searchSunnahs(query: String): List<Sunnah>
    suspend fun updateLastSeen(sunnahId: String, timestamp: Long)
}