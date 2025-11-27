package com.calyrsoft.ucbp1.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Maneja el almacenamiento de:
 * - La última hora sincronizada (UTC en ms).
 * - La hora del dispositivo en el momento de esa sincronización.
 */
class TimeDataStoreManager(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val LAST_SYNC_UTC_MILLIS = longPreferencesKey("last_sync_utc_millis")
        private val LAST_DEVICE_MILLIS_AT_SYNC = longPreferencesKey("last_device_millis_at_sync")
    }

    val lastSyncUtcMillis: Flow<Long?> = dataStore.data
        .map { prefs -> prefs[LAST_SYNC_UTC_MILLIS] }

    val lastDeviceMillisAtSync: Flow<Long?> = dataStore.data
        .map { prefs -> prefs[LAST_DEVICE_MILLIS_AT_SYNC] }

    /**
     * Guarda la hora del servidor y la hora local del dispositivo
     * en el momento de la sincronización.
     */
    suspend fun saveSyncData(syncUtcMillis: Long, deviceMillis: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_SYNC_UTC_MILLIS] = syncUtcMillis
            prefs[LAST_DEVICE_MILLIS_AT_SYNC] = deviceMillis
        }
    }
}
