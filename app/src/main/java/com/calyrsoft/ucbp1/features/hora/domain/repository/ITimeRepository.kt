package com.calyrsoft.ucbp1.features.hora.domain.repository

interface ITimeRepository {
    /**
     * Obtiene la hora actual en milisegundos.
     * La implementación se encarga de la lógica de caché y obtención de la red.
     * @return La hora actual como un Long en milisegundos, o 0L si ocurre un error.
     */
    suspend fun getCurrentTimeMillis(): Long
}