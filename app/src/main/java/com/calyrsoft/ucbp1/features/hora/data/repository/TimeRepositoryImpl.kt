package com.calyrsoft.ucbp1.features.hora.data.repository

import com.calyrsoft.ucbp1.datastore.TimeDataStoreManager
import com.calyrsoft.ucbp1.features.hora.data.api.dto.TimeApiService
import com.calyrsoft.ucbp1.features.hora.domain.repository.ITimeRepository
import kotlinx.coroutines.flow.first
import java.io.IOException
import kotlin.math.max

/**
 * Lógica:
 *  - ONLINE: usa API, guarda (hora servidor + hora dispositivo).
 *  - OFFLINE: reconstruye la hora actual sumando el tiempo transcurrido
 *             desde la última sincronización local.
 */
class TimeRepositoryImpl(
    private val timeApiService: TimeApiService,
    private val timeDataStoreManager: TimeDataStoreManager
) : ITimeRepository {

    // Caché opcional en memoria
    private var inMemoryTimestamp: Long = 0L

    override suspend fun getCurrentTimeMillis(): Long {
        return try {
            // 🔹 1) Intentamos SIEMPRE usar la API
            val remoteUtcMillis = timeApiService.getCurrentTime().unixtime * 1000L
            val deviceNow = System.currentTimeMillis()

            // Guardamos ambos valores
            timeDataStoreManager.saveSyncData(
                syncUtcMillis = remoteUtcMillis,
                deviceMillis = deviceNow
            )

            inMemoryTimestamp = remoteUtcMillis
            remoteUtcMillis
        } catch (e: IOException) {
            // 🔻 SIN INTERNET: reconstruimos en base a lo último guardado

            if (inMemoryTimestamp > 0L) {
                // Si ya teníamos algo en memoria en esta sesión, devolvemos eso
                return inMemoryTimestamp
            }

            val lastSyncUtc = timeDataStoreManager.lastSyncUtcMillis.first()
            val lastDeviceAtSync = timeDataStoreManager.lastDeviceMillisAtSync.first()

            if (lastSyncUtc != null && lastSyncUtc > 0L &&
                lastDeviceAtSync != null && lastDeviceAtSync > 0L
            ) {
                val deviceNow = System.currentTimeMillis()
                val rawDelta = deviceNow - lastDeviceAtSync
                // Por seguridad, evitamos deltas negativos si el usuario cambió la hora atrás
                val delta = max(rawDelta, 0L)

                // ⏱ Hora aproximada actual en UTC (basada en último sync + tiempo transcurrido local)
                val approxUtcNow = lastSyncUtc + delta

                // Actualizamos caché y (opcional) guardamos nueva base
                inMemoryTimestamp = approxUtcNow
                timeDataStoreManager.saveSyncData(
                    syncUtcMillis = approxUtcNow,
                    deviceMillis = deviceNow
                )

                approxUtcNow
            } else {
                // No tenemos info previa
                0L
            }
        }
    }
}
