package com.calyrsoft.ucbp1.features.hora.domain.usecase

import com.calyrsoft.ucbp1.features.hora.domain.repository.ITimeRepository


/**
 * Caso de uso para obtener la hora actual.
 * Delega la lógica de obtención al repositorio.
 */
class GetCurrentTimeUseCase(private val timeRepository: ITimeRepository) {
    suspend operator fun invoke(): Long {
        return timeRepository.getCurrentTimeMillis()
    }
}