package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Сущность для хранения информации о заправках топливом.
 */
@Entity(
    tableName = "refuels",
    foreignKeys = [
        ForeignKey(
            entity = CadenceEntity::class,
            parentColumns = ["id"],
            childColumns = ["cadenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cadenceId"]),
        Index(value = ["trailerNumber"])
    ]
)
data class RefuelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID каденции */
    val cadenceId: Long,

    /** Порядковый номер заправки в рамках каденции */
    val refuelNumber: Int = 0,

    /** Дата и время заправки */
    val date: Date,

    /** Местоположение (АЗС, город) */
    val location: String,

    /** Количество заправленного топлива в тягач (литры) */
    val truckFuel: Int,

    /** Количество заправленного AdBlue (литры) */
    val adBlue: Int,

    /** Количество заправленного топлива в прицеп/реф (литры) */
    val trailerFuel: Int,

    /** Номер прицепа на момент заправки */
    val trailerNumber: String?,

    /** Название топливной карты, использованной для оплаты */
    val cardName: String
)
