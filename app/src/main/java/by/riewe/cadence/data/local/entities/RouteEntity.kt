package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Сущность для хранения информации о конкретном маршруте (рейсе).
 */
@Entity(
    tableName = "routes",
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
        Index(value = ["routeNumber", "cadenceId"], unique = true),
        Index(value = ["isActive", "cadenceId"])
    ]
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cadenceId: Long,

    /** Порядковый номер маршрута внутри каденции */
    val routeNumber: Int,

    /** Статус маршрута: true - в пути, false - завершен */
    val isActive: Boolean = true,

    // --- Начало маршрута ---
    
    /** Место отправления */
    val startLocation: String,

    /** Дата и время отправления */
    val startDate: Date,

    /** Показания одометра при старте */
    val startOdometer: Int,

    /** Моточасы при старте */
    val startEngineHours: Int,

    /** Описание груза */
    val goodsDescription: String,

    // --- Конец маршрута (заполняется при финише) ---

    /** Дата и время прибытия */
    val endDate: Date? = null,

    /** Место прибытия */
    val endLocation: String? = null,

    /** Показания одометра при финише */
    val endOdometer: Int? = null,

    /** Моточасы при финише */
    val endEngineHours: Int? = null,

    // --- Характеристики ---

    /** Вес груза */
    val weight: Double? = null,

    /** Номер CMR накладной */
    val cmrNumber: String? = null,

    /** Температурный режим */
    val temperatureValue: String? = null,

    /** Режим работы агрегата (например, "Старт-Стоп", "Непрерывный") */
    val agregateMode: String = "Выключен",

    /** Номер прицепа на данном маршруте */
    val trailerNumber: String? = null,

    // --- Расчетные значения ---

    /** Пройденное расстояние за маршрут */
    val mileAge: Int? = null,

    /** Средний расход топлива */
    val fuelConsumption: Double? = null,

    /** Потрачено топлива всего */
    val fuelBurned: Double? = null,

    /** Отработано моточасов за рейс */
    val totalEngineHours: Int? = null
)
