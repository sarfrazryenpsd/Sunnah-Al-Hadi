package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetSunnahByIdUseCase(private val sunnahRepository: SunnahRepository) {
    suspend operator fun invoke(id: String): Sunnah? {
        return when (val result = sunnahRepository.getSunnahById(id)) {
            is RepositoryResult.Success -> result.data
            is RepositoryResult.Error -> {
                // Handle error
                null
            }
        }
    }

}