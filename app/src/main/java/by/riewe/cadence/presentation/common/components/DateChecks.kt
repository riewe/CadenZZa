package by.riewe.cadence.presentation.common.components

import by.riewe.cadence.data.local.entities.CadenceEntity
import java.util.Calendar
import java.util.Date

/**
 * Проверяет, попадает ли указанная дата в период любой из СУЩЕСТВУЮЩИХ каденций.
 * Используется для предотвращения наложения каденций друг на друга.
 * 
 * @param ignoreId ID каденции, которую не нужно учитывать (обычно текущая редактируемая).
 * @return true если дата "занята" другой каденцией.
 */
fun CheckDate(
    date: Date,
    cadences: List<CadenceEntity>,
    ignoreId: Long? = null
): Boolean {
    val checkTime = normalizeDate(date).time

    return cadences.any { cadence ->
        if (ignoreId != null && cadence.id == ignoreId) return@any false

        val startTime = normalizeDate(cadence.startDate).time
        // Если каденция еще не завершена (endDate == null), считаем, что она длится по сей день
        val endTime = cadence.endDate?.let { normalizeDate(it).time } ?: normalizeDate(Date()).time

        checkTime in startTime..endTime
    }
}

/**
 * Перегрузка CheckDate для работы с Long (timestamp).
 */
fun CheckDate(
    timestamp: Long,
    cadences: List<CadenceEntity>,
    ignoreId: Long? = null
): Boolean = CheckDate(Date(timestamp), cadences, ignoreId)

/**
 * Проверяет, находится ли дата ВНУТРИ границ конкретной каденции.
 * Используется при создании рейсов, заправок и перецепов, чтобы они не выходили за рамки каденции.
 * 
 * @return true если дата корректна (попадает в границы каденции).
 */
fun IsDateWithinCadence(
    date: Date,
    cadence: CadenceEntity
): Boolean {
    val checkTime = normalizeDate(date).time
    val startTime = normalizeDate(cadence.startDate).time
    val endTime = cadence.endDate?.let { normalizeDate(it).time } ?: normalizeDate(Date()).time

    return checkTime in startTime..endTime
}

/**
 * Перегрузка IsDateWithinCadence для работы с Long.
 */
fun IsDateWithinCadence(
    timestamp: Long,
    cadence: CadenceEntity
): Boolean = IsDateWithinCadence(Date(timestamp), cadence)

/**
 * Вспомогательная функция для обнуления времени в объекте Date.
 */
private fun normalizeDate(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
