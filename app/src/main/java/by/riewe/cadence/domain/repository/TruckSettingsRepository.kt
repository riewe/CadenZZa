package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.TruckSettingsEntity
import kotlinx.coroutines.flow.Flow

interface TruckSettingsRepository {
    fun getSettingsForTruck(truckNumber: String): Flow<TruckSettingsEntity?>
    suspend fun saveSettings(settings: TruckSettingsEntity)
}
