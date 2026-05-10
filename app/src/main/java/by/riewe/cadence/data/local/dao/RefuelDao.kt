package by.riewe.cadence.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.data.model.DetailedCardStat
import by.riewe.cadence.data.model.TrailerCadenceStat
import by.riewe.cadence.data.model.TrailerRefuelStat
import kotlinx.coroutines.flow.Flow

@Dao
interface RefuelDao {

    // ============================================================
    // ПОЛУЧЕНИЕ ДАННЫХ
    // ============================================================


    @Query("SELECT * FROM refuels WHERE cadenceId = :cadenceId ORDER BY date ASC")
    fun getRefuelsByCadence(cadenceId: Long): Flow<List<RefuelEntity>>

    @Query("SELECT * FROM refuels WHERE cadenceId = :cadenceId ORDER BY date ASC")
    suspend fun getRefuelsByCadenceList(cadenceId: Long): List<RefuelEntity>

    @Query("SELECT * FROM refuels WHERE id = :refuelId LIMIT 1")
    suspend fun getRefuelById(refuelId: Long): RefuelEntity?

    @Query("SELECT * FROM refuels WHERE trailerNumber = :trailerNumber ORDER BY date ASC")
    fun getRefuelsByTrailer(trailerNumber: String): Flow<List<RefuelEntity>>

    @Query("""
        SELECT * FROM refuels 
        WHERE trailerNumber = :trailerNumber 
        ORDER BY date DESC 
        LIMIT 1
    """)
    suspend fun getLastRefuelOfTrailer(trailerNumber: String): RefuelEntity?

    @Query("SELECT MAX(refuelNumber) FROM refuels WHERE cadenceId = :cadenceId")
    suspend fun getMaxRefuelNumber(cadenceId: Long): Int?

    // ============================================================
    // ВСТАВКА И ОБНОВЛЕНИЕ
    // ============================================================

    @Insert
    suspend fun insert(refuel: RefuelEntity): Long

    @Update
    suspend fun update(refuel: RefuelEntity)

    @Transaction
    suspend fun reorderRefuels(cadenceId: Long) {
        val refuels = getRefuelsByCadenceList(cadenceId)
        refuels.forEachIndexed { index, refuel ->
            val expectedNumber = index + 1
            if (refuel.refuelNumber != expectedNumber) {
                update(refuel.copy(refuelNumber = expectedNumber))
            }
        }
    }

    @Transaction
    suspend fun deleteAndReorder(refuelId: Long) {
        val refuel = getRefuelById(refuelId) ?: return
        val cadenceId = refuel.cadenceId
        deleteRefuel(refuelId)
        reorderRefuels(cadenceId)
    }

    // ============================================================
    // СТАТИСТИКА ПО ПРИЦЕПАМ
    // ============================================================

    @Query("""
        SELECT SUM(trailerFuel) 
        FROM refuels 
        WHERE cadenceId = :cadenceId AND trailerNumber = :trailerNumber
    """)
    suspend fun getTrailerFuelByCadence(cadenceId: Long, trailerNumber: String): Int?

    @Query("""
        SELECT 
            trailerNumber as trailerNumber,
            COUNT(*) as refuelCount,
            SUM(trailerFuel) as totalFuel,
            MIN(date) as firstRefuel,
            MAX(date) as lastRefuel
        FROM refuels 
        WHERE cadenceId = :cadenceId AND trailerNumber IS NOT NULL
        GROUP BY trailerNumber
    """)
    suspend fun getTrailerRefuelStats(cadenceId: Long): List<TrailerRefuelStat>

    @Query("""
        SELECT 
            trailerNumber as trailerNumber,
            SUM(trailerFuel) as totalFuel,
            COUNT(*) as refuelCount
        FROM refuels 
        WHERE cadenceId = :cadenceId AND trailerNumber IS NOT NULL
        GROUP BY trailerNumber
    """)
    suspend fun getCadenceTrailerStats(cadenceId: Long): List<TrailerCadenceStat>

    // ============================================================
    // ОБЩАЯ СТАТИСТИКА
    // ============================================================

    @Query("SELECT SUM(truckFuel) FROM refuels WHERE cadenceId = :cadenceId")
    suspend fun getTotalTruckFuelByCadence(cadenceId: Long): Int?

    @Query("SELECT SUM(adBlue) FROM refuels WHERE cadenceId = :cadenceId")
    suspend fun getTotalAdBlueByCadence(cadenceId: Long): Int?

    @Query("SELECT SUM(trailerFuel) FROM refuels WHERE cadenceId = :cadenceId")
    suspend fun getTotalTrailerFuelByCadence(cadenceId: Long): Int?

    @Query("""
    SELECT SUM(trailerFuel) FROM refuels 
    WHERE trailerNumber = :trailerNumber 
    AND date BETWEEN :startTime AND :endTime
""")
    suspend fun getTrailerFuelVolumeForTimeRange(
        trailerNumber: String,
        startTime: Long,
        endTime: Long
    ): Int?

    @Query("""
        SELECT 
            cardName as cardName,
            trailerNumber as trailerNumber,
            COUNT(*) as refuelCount,
            SUM(truckFuel) as totalTruckFuel,
            SUM(adBlue) as totalAdBlue,
            SUM(trailerFuel) as totalTrailerFuel
        FROM refuels 
        WHERE cadenceId = :cadenceId AND trailerNumber IS NOT NULL
        GROUP BY cardName, trailerNumber
    """)
    suspend fun getDetailedCardStats(cadenceId: Long): List<DetailedCardStat>

    // ============================================================
    // УДАЛЕНИЕ
    // ============================================================

    @Query("DELETE FROM refuels WHERE id = :refuelId")
    suspend fun deleteRefuel(refuelId: Long)

    @Query("DELETE FROM refuels WHERE cadenceId = :cadenceId")
    suspend fun deleteRefuelsByCadence(cadenceId: Long)
}