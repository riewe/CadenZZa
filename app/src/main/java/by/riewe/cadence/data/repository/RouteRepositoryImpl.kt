package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.RouteDao
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.model.RouteWeightStatistics
import by.riewe.cadence.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Реализация репозитория для работы с маршрутами.
 */
class RouteRepositoryImpl @Inject constructor(
    private val routeDao: RouteDao
) : RouteRepository {

    override fun getRoutesByCadence(cadenceId: Long): Flow<List<RouteEntity>> =
        routeDao.getRoutesByCadence(cadenceId)

    override suspend fun getActiveRoute(cadenceId: Long): RouteEntity? =
        routeDao.getActiveRoute(cadenceId)

    override suspend fun getActiveRouteByCadence(cadenceId: Long): RouteEntity? =
        routeDao.getActiveRouteByCadence(cadenceId)

    override suspend fun getRouteById(routeId: Long): RouteEntity? =
        routeDao.getRouteById(routeId)

    override suspend fun getLastRouteByCadence(cadenceId: Long): RouteEntity? =
        routeDao.getLastRouteByCadence(cadenceId)

    override suspend fun addRoute(route: RouteEntity): Long =
        routeDao.insert(route)

    override suspend fun updateRoute(route: RouteEntity) =
        routeDao.update(route)

    override suspend fun getNextRouteNumber(cadenceId: Long): Int =
        (routeDao.getMaxRouteNumber(cadenceId) ?: 0) + 1

    override suspend fun getTotalFuelBurned(cadenceId: Long): Int =
        routeDao.getTotalFuelBurnedByCadence(cadenceId)?.toInt() ?: 0

    override suspend fun getTotalMileageByCadence(cadenceId: Long): Int =
        routeDao.getTotalMileageByCadence(cadenceId)?.toInt() ?: 0

    override suspend fun getLoadedMileageByCadence(cadenceId: Long): Long? =
        routeDao.getLoadedMileageByCadence(cadenceId)

    override suspend fun getEmptyMileageByCadence(cadenceId: Long): Long? =
        routeDao.getEmptyMileageByCadence(cadenceId)

    override suspend fun getMileageByWeightCategory(cadenceId: Long): List<RouteWeightStatistics> =
        routeDao.getMileageByWeightCategory(cadenceId)

    override suspend fun getAverageFuelConsumption(cadenceId: Long): Double? =
        routeDao.getAverageFuelConsumption(cadenceId)

    override suspend fun getAverageWeight(cadenceId: Long): Double? =
        routeDao.getAverageWeight(cadenceId)

    override suspend fun getTotalFuelBurnedByCadence(cadenceId: Long): Double =
        routeDao.getTotalFuelBurnedByCadence(cadenceId) ?: 0.0

    override suspend fun deleteRouteById(routeId: Long) =
        routeDao.deleteRouteById(routeId)

    override suspend fun deleteAndReorderRoutes(routeId: Long) =
        routeDao.deleteAndReorder(routeId)

    override suspend fun reorderRoutes(cadenceId: Long) =
        routeDao.reorderRoutes(cadenceId)

    override suspend fun deleteRoutesByCadence(cadenceId: Long) =
        routeDao.deleteRoutesByCadence(cadenceId)

    override suspend fun closeRouteWithCalculation(
        routeId: Long,
        endLocation: String,
        endOdometer: Int,
        endEngineHours: Int,
        mileAge: Int,
        totalEngineHours: Int,
        fuelConsumption: Double,
        fuelBurned: Double
    ) {
        routeDao.closeRouteWithCalculation(
            routeId, endLocation, endOdometer, endEngineHours,
            mileAge, totalEngineHours, fuelConsumption, fuelBurned
        )
    }
}
