package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.CadenceDao
import by.riewe.cadence.data.local.entities.CadenceEntity
import by.riewe.cadence.domain.repository.CadenceRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

/**
 * Реализация репозитория для работы с каденциями.
 */
class CadenceRepositoryImpl @Inject constructor(
    private val cadenceDao: CadenceDao
) : CadenceRepository {

    override fun getAllCadences(): Flow<List<CadenceEntity>> {
        return cadenceDao.getAllCadences()
    }

    override fun getCadenceById(id: Long): Flow<CadenceEntity?> {
        return cadenceDao.getCadenceById(id)
    }

    override suspend fun getActiveCadence(): CadenceEntity? {
        return cadenceDao.getActiveCadence()
    }

    override suspend fun getNextCadenceNumber(): Int {
        return (cadenceDao.getMaxCadenceNumber() ?: 0) + 1
    }

    override suspend fun startCadence(cadence: CadenceEntity): Long {
        return cadenceDao.insert(cadence)
    }

    override suspend fun updateCadence(cadence: CadenceEntity) {
        cadenceDao.update(cadence)
    }

    /**
     * Закрывает текущую каденцию и рассчитывает итоги (пробег и длительность).
     */
    override suspend fun closeCadence(
        cadenceId: Long,
        endDate: Date,
        endTime: Long,
        finalOdometer: Int,
        finalTruckFuel: Int,
        finalTrailerFuel: Int,
        finalEngineHours: Int
    ) {
        val active = cadenceDao.getActiveCadence()

        if (active != null && active.id == cadenceId) {
            // Расчет пробега
            val mileage = finalOdometer - active.initialOdometer

            // Расчет длительности в днях
            val diffInMillis = endDate.time - active.startDate.time
            val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

            // Обновляем запись в БД через DAO
            cadenceDao.closeCadence(
                cadenceId = cadenceId,
                endDate = endDate.time,
                endTime = endTime,
                finalOdometer = finalOdometer,
                finalTruckFuel = finalTruckFuel,
                finalTrailerFuel = finalTrailerFuel,
                finalEngineHours = finalEngineHours,
                totalMileage = mileage,
                totalDays = days
            )
        }
    }

    override suspend fun deleteCadence(id: Long) {
        cadenceDao.deleteById(id)
    }
}
