package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.util.Result
import javax.inject.Inject

class GetSunnahByIdUseCase @Inject constructor(
    private val sunnahRepository: SunnahRepository
) : UseCase<String, Result<Sunnah?>>() {
    override suspend fun execute(parameters: String): Result<Sunnah?> {
        return sunnahRepository.getSunnahById(parameters)
    }
}