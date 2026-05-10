package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "truck_settings")
data class TruckSettingsEntity(
    @PrimaryKey
    val truckNumber: String,
    val baseConsumption: Double = 21.0,
    val weightCoefficient: Double = 0.3
)
