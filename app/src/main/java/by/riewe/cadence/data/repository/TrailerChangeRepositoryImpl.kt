package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.TrailerChangeDao
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.repository.TrailerChangeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

/**
 * Реализация репозитория для работы с заменами прицепов.
 */
class TrailerChangeRepositoryImpl @Inject constructor(
    private val trailerChangeDao: TrailerChangeDao
) : TrailerChangeRepository {

    override fun getChangesByCadence(cadenceId: Long): Flow<List<TrailerChangeEntity>> {
        return trailerChangeDao.getChangesByCadence(cadenceId)
    }

    override suspend fun getChangeById(id: Long): TrailerChangeEntity? {
        return trailerChangeDao.getChangeById(id)
    }

    override suspend fun deleteTrailerChange(change: TrailerChangeEntity) {
        trailerChangeDao.delete(change)
    }

    override suspend fun getMaxChangeNumber(cadenceId: Long): Int? {
        return trailerChangeDao.getMaxChangeNumber(cadenceId)
    }

    override suspend fun getActiveChange(cadenceId: Long): TrailerChangeEntity? =
        trailerChangeDao.getActiveChange(cadenceId)

    override suspend fun getChangeAtMoment(cadenceId: Long, timestamp: Long): TrailerChangeEntity? =
        trailerChangeDao.getChangeAtMoment(cadenceId, timestamp)

    override suspend fun addTrailerChange(change: TrailerChangeEntity): Long =
        trailerChangeDao.insert(change)

    override suspend fun updateTrailerChange(change: TrailerChangeEntity) {
        trailerChangeDao.update(change)
    }

    override suspend fun getNextChangeNumber(cadenceId: Long): Int =
        trailerChangeDao.getMaxChangeNumber(cadenceId)?.let { it + 1 } ?: 0

    /**
     * Закрывает текущую замену прицепа.
     * @param endDate Объект Date (конвертируется в Long для DAO).
     */
    override suspend fun closeTrailerChange(
        changeId: Long,
        endDate: Date,
        endTrailerFuel: Int?,
        endEngineHours: Int?,
        endLocation: String?
    ) {
        // Мы используем .time для передачи в SQL-запрос DAO
        trailerChangeDao.closeChange(
            changeId = changeId,
            endDate = endDate.time,
            endTrailerFuel = endTrailerFuel,
            endEngineHours = endEngineHours,
            endLocation = endLocation,
            totalEngineHours = null // Здесь можно добавить логику расчета разницы
        )
    }

    override suspend fun getChangesByCadenceOrdered(cadenceId: Long): List<TrailerChangeEntity> {
        return trailerChangeDao.getChangesByCadenceOrdered(cadenceId)
    }

    override fun getTrailerHistoryInCadence(
        cadenceId: Long,
        trailerNumber: String
    ): Flow<List<TrailerChangeEntity>> {
        return trailerChangeDao.getTrailerHistoryInCadence(cadenceId, trailerNumber)
    }

    override suspend fun reorderChanges(cadenceId: Long) {
        trailerChangeDao.reorderChanges(cadenceId)
    }
}
