package com.calyrsoft.ucbp1.features.hora.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calyrsoft.ucbp1.features.hora.domain.usecase.GetCurrentTimeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TimeUiState(
    val formattedTime: String = "Cargando...",
    val isLoading: Boolean = true,
    val error: String? = null
)

class TimeViewModel(
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase
) : ViewModel() {

    var uiState by mutableStateOf(TimeUiState())
        private set

    private var baseSyncedTimeMillis: Long = 0L
    private var baseDeviceTimeMillis: Long = 0L

    private var tickerJob: Job? = null

    init {
        fetchCurrentTime()
    }

    fun fetchCurrentTime() {
        viewModelScope.launch {
            tickerJob?.cancel()
            uiState = uiState.copy(isLoading = true, error = null)

            val syncedTime = getCurrentTimeUseCase()

            if (syncedTime > 0L) {
                baseSyncedTimeMillis = syncedTime
                baseDeviceTimeMillis = System.currentTimeMillis()
                startTicker()
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo obtener la hora. Verifica tu conexión a internet."
                )
            }
        }
    }

    private fun startTicker() {
        tickerJob = viewModelScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - baseDeviceTimeMillis
                val currentTime = baseSyncedTimeMillis + elapsed

                uiState = uiState.copy(
                    isLoading = false,
                    error = null,
                    formattedTime = formatMillisToDateTime(currentTime)
                )

                delay(1000L)
            }
        }
    }

    private fun formatMillisToDateTime(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("America/La_Paz")
        return format.format(date)
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}
