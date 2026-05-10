package by.riewe.cadence.data.local.dao

import androidx.room.*
import by.riewe.cadence.data.local.entities.TruckSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TruckSettingsDao {
    @Query("SELECT * FROM truck_settings WHERE truckNumber = :truckNumber")
    fun getSettingsForTruck(truckNumber: String): Flow<TruckSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: TruckSettingsEntity)
}
