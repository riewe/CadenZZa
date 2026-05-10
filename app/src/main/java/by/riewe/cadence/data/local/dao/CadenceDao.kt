package by.riewe.cadence.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.riewe.cadence.data.local.entities.CadenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CadenceDao {
    //Список каденций
    @Query("SELECT * FROM cadences ORDER BY startDate DESC")
    fun getAllCadences(): Flow<List<CadenceEntity>>

    //Получение конкретной каденции по ID
    @Query("SELECT * FROM cadences WHERE id = :id")
    fun getCadenceById(id: Long): Flow<CadenceEntity?>

    //Текущая каденция
    @Query("SELECT * FROM cadences WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveCadence(): CadenceEntity?

    //Номер последней каденции
    @Query("SELECT MAX(cadenceNumber) FROM cadences")
    suspend fun getMaxCadenceNumber(): Int?

    @Insert
    suspend fun insert(cadence: CadenceEntity): Long

    @Update
    suspend fun update(cadence: CadenceEntity)

    @Query("UPDATE cadences SET endDate = :endDate, endTime = :endTime, finalOdometer = :finalOdometer, " +
            "finalTruckFuel = :finalTruckFuel, finalTrailerFuel = :finalTrailerFuel, finalEngineHours = :finalEngineHours," +
            "totalMileage = :totalMileage, totalDays = :totalDays, isActive = 0 WHERE id = :cadenceId")
    suspend fun closeCadence(
        cadenceId: Long,
        endDate: Long,
        endTime: Long,
        finalOdometer: Int,
        finalTruckFuel: Int,
        finalTrailerFuel: Int,
        finalEngineHours: Int,
        totalMileage: Int,
        totalDays: Int
        )

    @Query("DELETE FROM cadences WHERE id = :id")
    suspend fun deleteById(id: Long)
}