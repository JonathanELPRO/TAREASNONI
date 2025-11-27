package com.calyrsoft.ucbp1.features.hora.data.api.dto
import retrofit2.http.GET

/**
 * Interfaz de servicio para Retrofit que define cómo obtener la hora desde la API.
 */
interface TimeApiService {
    /**
     * Obtiene la hora actual en formato UTC desde la API pública.
     * La respuesta es un objeto WorldTimeDto.
     */
    @GET("api/timezone/Etc/UTC")
    suspend fun getCurrentTime(): WorldTimeDto
}