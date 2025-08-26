package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    //suspend fun getBookmarkedSunnahs(): Result<List<Sunnah>>
    suspend fun toggleBookmark(sunnahId: String): Result<Boolean>
    fun getBookmarkedSunnahsFlow(): Flow<Result<List<Sunnah>>>

}