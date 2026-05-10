package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.domain.model.DetailedFuelBalance
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Интерфейс репозитория для работы с заправками.
 */
interface RefuelRepository {
    
    /**
     * Поток всех заправок за каденцию.
     */
    fun getRefuelsByCadence(cadenceId: Long): Flow<List<RefuelEntity>>

    /**
     * Добавляет новую запись о заправке.
     * @param date Объект Date вместо Long.
     */
    suspend fun addRefuel(
        cadenceId: Long,
        date: Date,
        location: String,
        truckFuel: Int,
        adBlue: Int,
        trailerFuel: Int,
        cardName: String
    ): Long

    /**
     * Получает детальный баланс топлива за каденцию.
     */
    suspend fun getDetailedFuelBalance(
        cadenceId: Long,
        initialTruckFuel: Int
    ): DetailedFuelBalance

    /**
     * Проверяет, можно ли заправить прицеп в данном периоде (есть ли активный прицеп).
     */
    suspend fun canRefuelTrailer(cadenceId: Long): Boolean

    suspend fun deleteRefuel(refuelId: Long)
    suspend fun reorderRefuels(cadenceId: Long)
}
