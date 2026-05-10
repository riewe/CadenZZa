package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trailer_changes",
    indices = [
        Index(value = ["cadenceId"]),
        Index(value = ["changeNumber", "cadenceId"], unique = true)
    ]
)
data class TrailerChangeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cadenceId: Long,

    val changeNumber: Int,

    val startDate: Long,
    val trailerNumber: String,
    val donorTruckNumber: String,
    val startTrailerFuel: Int,
    val startEngineHours: Int,
    val startLocation: String,

    val endDate: Long? = null,
    val endTrailerFuel: Int? = null,
    val endEngineHours: Int? = null,
    val endLocation: String? = null,

    // Вычисляемое значение — заполняется при закрытии
    val totalEngineHours: Int? = null,  

    val isActive: Boolean = true
)