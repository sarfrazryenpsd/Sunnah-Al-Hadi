package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.util.Result

class GetAllSunnahsUseCase (
    private val sunnahRepository: SunnahRepository
): UseCase<Unit, Result<List<Sunnah>>>() {
    override suspend fun execute(parameters: Unit): Result<List<Sunnah>> {
        return sunnahRepository.getAllSunnahs()
    }
}

