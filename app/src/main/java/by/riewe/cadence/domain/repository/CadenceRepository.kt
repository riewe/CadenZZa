package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.CadenceEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Интерфейс репозитория для работы с каденциями.
 */
interface CadenceRepository {

    /**
     * Получает поток всех каденций для отображения в списке.
     */
    fun getAllCadences(): Flow<List<CadenceEntity>>

    /**
     * Получает поток конкретной каденции по ID.
     */
    fun getCadenceById(id: Long): Flow<CadenceEntity?>

    /**
     * Получает текущую активную каденцию (если есть).
     */
    suspend fun getActiveCadence(): CadenceEntity?

    /**
     * Возвращает номер для новой каденции (на 1 больше максимального существующего).
     */
    suspend fun getNextCadenceNumber(): Int

    /**
     * Создает новую каденцию.
     * @return ID созданной записи.
     */
    suspend fun startCadence(cadence: CadenceEntity): Long

    /**
     * Обновляет данные каденции.
     */
    suspend fun updateCadence(cadence: CadenceEntity)

    /**
     * Закрывает каденцию, рассчитывает итоги и снимает флаг isActive.
     * @param endDate Дата окончания (Date).
     * @param endTime Время окончания в миллисекундах с начала дня.
     */
    suspend fun closeCadence(
        cadenceId: Long,
        endDate: Date,
        endTime: Long,
        finalOdometer: Int,
        finalTruckFuel: Int,
        finalTrailerFuel: Int,
        finalEngineHours: Int
    )

    /**
     * Удаляет каденцию по ID.
     */
    suspend fun deleteCadence(id: Long)
}
