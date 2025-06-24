package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetSunnahByIdUseCase(private val sunnahRepository: SunnahRepository) {
    suspend operator fun invoke(id: String): Sunnah? {
        return when (val result = sunnahRepository.getSunnahById(id)) {
            is Result.Success -> result.data
            is Result.Error -> {
                // Handle error
                null
            }
        }
    }

}