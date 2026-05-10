package by.riewe.cadence.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailerChangeDao {
    // Все замены прицепа для периода
    @Query("SELECT * FROM trailer_changes WHERE cadenceId = :cadenceId ORDER BY changeNumber")
    fun getChangesByCadence(cadenceId: Long): Flow<List<TrailerChangeEntity>>

    // Текущая активная замена
    @Query("SELECT * FROM trailer_changes WHERE isActive = 1 AND cadenceId = :cadenceId LIMIT 1")
    suspend fun getActiveChange(cadenceId: Long): TrailerChangeEntity?

    // Последний номер замены в периоде
    @Query("SELECT MAX(changeNumber) FROM trailer_changes WHERE cadenceId = :cadenceId")
    suspend fun getMaxChangeNumber(cadenceId: Long): Int?

    @Insert
    suspend fun insert(change: TrailerChangeEntity): Long

    @Query("SELECT * FROM trailer_changes WHERE id = :id")
    suspend fun getChangeById(id: Long): TrailerChangeEntity?

    @Update
    suspend fun update(change: TrailerChangeEntity)

    @Transaction
    suspend fun reorderChanges(cadenceId: Long) {
        val changes = getChangesByCadenceOrdered(cadenceId)
        changes.forEachIndexed { index, change ->
            val expectedNumber = index
            if (change.changeNumber != expectedNumber) {
                update(change.copy(changeNumber = expectedNumber))
            }
        }
    }

    @Query("""
        UPDATE trailer_changes 
        SET endDate = :endDate, 
            endTrailerFuel = :endTrailerFuel, 
            endEngineHours = :endEngineHours, 
            endLocation = :endLocation,
            totalEngineHours = :totalEngineHours,
            isActive = 0 
        WHERE id = :changeId
    """)
    suspend fun closeChange(
        changeId: Long,
        endDate: Long,
        endTrailerFuel: Int?,
        endEngineHours: Int?,
        endLocation: String?,
        totalEngineHours: Int?
    )

    @Delete
    suspend fun delete(change: TrailerChangeEntity)

    /**
     * Получить замены периода с сортировкой по времени (для истории)
     */
    @Query("""
        SELECT * FROM trailer_changes 
        WHERE cadenceId = :cadenceId 
        ORDER BY startDate ASC
    """)
    suspend fun getChangesByCadenceOrdered(cadenceId: Long): List<TrailerChangeEntity>

    /**
     * Получить замену, активную на конкретный момент времени
     */
    @Query("""
        SELECT * FROM trailer_changes 
        WHERE cadenceId = :cadenceId 
          AND startDate <= :timestamp
          AND (endDate IS NULL OR endDate >= :timestamp)
        LIMIT 1
    """)
    suspend fun getChangeAtMoment(cadenceId: Long, timestamp: Long): TrailerChangeEntity?

    /**
     * Получить полную историю прицепа за каденцию
     */
    @Query("""
        SELECT * FROM trailer_changes 
        WHERE cadenceId = :cadenceId AND trailerNumber = :trailerNumber
        ORDER BY startDate ASC
    """)
    fun getTrailerHistoryInCadence(
        cadenceId: Long,
        trailerNumber: String
    ): Flow<List<TrailerChangeEntity>>
}
