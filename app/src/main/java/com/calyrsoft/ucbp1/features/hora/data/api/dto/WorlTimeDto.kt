package com.calyrsoft.ucbp1.features.hora.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) para la respuesta de la API de WorldTimeAPI.
 * Solo nos interesa el campo 'unixtime'.
 */
data class WorldTimeDto(
    @SerializedName("unixtime") val unixtime: Long
)
