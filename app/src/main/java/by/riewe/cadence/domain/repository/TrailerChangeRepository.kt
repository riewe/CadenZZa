package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Интерфейс репозитория для работы с заменами прицепов.
 */
interface TrailerChangeRepository {
    // Получение данных
    fun getChangesByCadence(cadenceId: Long): Flow<List<TrailerChangeEntity>>
    suspend fun getChangeById(id: Long): TrailerChangeEntity?
    suspend fun deleteTrailerChange(change: TrailerChangeEntity)
    suspend fun getMaxChangeNumber(cadenceId: Long): Int?
    suspend fun getActiveChange(cadenceId: Long): TrailerChangeEntity?
    suspend fun getChangeAtMoment(cadenceId: Long, timestamp: Long): TrailerChangeEntity?

    // Операции
    suspend fun addTrailerChange(change: TrailerChangeEntity): Long
    suspend fun updateTrailerChange(change: TrailerChangeEntity)
    suspend fun getNextChangeNumber(cadenceId: Long): Int

    /**
     * Закрывает текущую замену прицепа (отцепка).
     * @param endDate Дата окончания (Date).
     */
    suspend fun closeTrailerChange(
        changeId: Long,
        endDate: Date,
        endTrailerFuel: Int?,
        endEngineHours: Int?,
        endLocation: String?
    )

    suspend fun getChangesByCadenceOrdered(cadenceId: Long): List<TrailerChangeEntity>
    fun getTrailerHistoryInCadence(
        cadenceId: Long,
        trailerNumber: String
    ): Flow<List<TrailerChangeEntity>>

    suspend fun reorderChanges(cadenceId: Long)
}
