package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.TruckSettingsDao
import by.riewe.cadence.data.local.entities.TruckSettingsEntity
import by.riewe.cadence.domain.repository.TruckSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TruckSettingsRepositoryImpl @Inject constructor(
    private val truckSettingsDao: TruckSettingsDao
) : TruckSettingsRepository {
    override fun getSettingsForTruck(truckNumber: String): Flow<TruckSettingsEntity?> {
        return truckSettingsDao.getSettingsForTruck(truckNumber)
    }

    override suspend fun saveSettings(settings: TruckSettingsEntity) {
        truckSettingsDao.insertOrUpdate(settings)
    }
}
