package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.model.RouteWeightStatistics
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Интерфейс репозитория для работы с маршрутами (рейсами).
 */
interface RouteRepository {
    // Потоки данных для UI
    fun getRoutesByCadence(cadenceId: Long): Flow<List<RouteEntity>>

    // Операции с маршрутами
    suspend fun getActiveRoute(cadenceId: Long): RouteEntity?
    suspend fun getActiveRouteByCadence(cadenceId: Long): RouteEntity?
    suspend fun getRouteById(routeId: Long): RouteEntity?
    suspend fun getLastRouteByCadence(cadenceId: Long): RouteEntity?
    suspend fun addRoute(route: RouteEntity): Long
    suspend fun updateRoute(route: RouteEntity)

    // Статистика (расходы и пробег)
    suspend fun getNextRouteNumber(cadenceId: Long): Int
    suspend fun getTotalFuelBurned(cadenceId: Long): Int
    suspend fun getTotalMileageByCadence(cadenceId: Long): Int
    suspend fun getLoadedMileageByCadence(cadenceId: Long): Long?
    suspend fun getEmptyMileageByCadence(cadenceId: Long): Long?
    suspend fun getMileageByWeightCategory(cadenceId: Long): List<RouteWeightStatistics>
    suspend fun getAverageFuelConsumption(cadenceId: Long): Double?
    suspend fun getAverageWeight(cadenceId: Long): Double?
    suspend fun getTotalFuelBurnedByCadence(cadenceId: Long): Double
    suspend fun deleteRouteById(routeId: Long)
    suspend fun deleteAndReorderRoutes(routeId: Long)
    suspend fun reorderRoutes(cadenceId: Long)
    suspend fun deleteRoutesByCadence(cadenceId: Long)

    /**
     * Закрывает маршрут и сохраняет расчетные данные.
     */
    suspend fun closeRouteWithCalculation(
        routeId: Long,
        endLocation: String,
        endOdometer: Int,
        endEngineHours: Int,
        mileAge: Int,
        totalEngineHours: Int,
        fuelConsumption: Double,
        fuelBurned: Double
    )
}
