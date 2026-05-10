package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Сущность для хранения информации о каденции в базе данных.
 * Каденция представляет собой период работы водителя от выезда до возвращения.
 */
@Entity(tableName = "cadences")
data class CadenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Порядковый номер каденции (например, 1, 2, 3...) */
    val cadenceNumber: Int,

    /** Дата начала каденции (объект Date, время обнулено до 00:00:00) */
    val startDate: Date,

    /** Время начала каденции (миллисекунды с начала суток выбранного дня) */
    val startTime: Long,

    /** Имя первого (основного) водителя */
    val driver1: String,

    /** Имя второго водителя (опционально) */
    val driver2: String?,

    /** Государственный номер тягача */
    val truckNumber: String,

    /** Начальное значение одометра (пробег) при старте каденции */
    val initialOdometer: Int,

    /** Начальное количество топлива в тягаче */
    val initialTruckFuel: Int,

    /** Государственный номер прицепа */
    val trailerNumber: String,

    /** Начальное количество топлива в холодильной установке (рефе) прицепа */
    val initialTrailerFuel: Int,

    /** Начальное количество моточасов холодильной установки */
    val initialEngineHours: Int,

    /** Дата завершения каденции (объект Date, время обнулено до 00:00:00) */
    val endDate: Date? = null,

    /** Время завершения каденции (миллисекунды с начала суток) */
    val endTime: Long? = null,

    /** Финальное значение одометра при закрытии каденции */
    val finalOdometer: Int? = null,

    /** Финальное количество топлива в тягаче */
    val finalTruckFuel: Int? = null,

    /** Финальное количество топлива в прицепе */
    val finalTrailerFuel: Int? = null,

    /** Финальные моточасы рефа */
    val finalEngineHours: Int? = null,

    /** Общий пробег за каденцию (рассчитывается как finalOdometer - initialOdometer) */
    val totalMileage: Int? = null,

    /** Общая продолжительность каденции в днях */
    val totalDays: Int? = null,

    /** Статус каденции: true - открыта (активна), false - закрыта */
    val isActive: Boolean = true
)
