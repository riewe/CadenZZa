package by.riewe.cadence.domain.usecase

import by.riewe.cadence.data.local.dao.RouteDao
import by.riewe.cadence.data.local.model.RouteWeightStatistics
import by.riewe.cadence.data.local.model.toDomain
import by.riewe.cadence.domain.model.MileageStats
import javax.inject.Inject

/**
 * ОДИН UseCase для всей статистики маршрутов
 */
class RouteStatisticsUseCase @Inject constructor(
    private val routeDao: RouteDao
) {
    /**
     * Полная статистика пробега по каденции
     */
    suspend fun getMileageStats(cadenceId: Long): MileageStats {
        val total = routeDao.getTotalMileageByCadence(cadenceId) ?: 0L
        val loaded = routeDao.getLoadedMileageByCadence(cadenceId) ?: 0L
        val empty = routeDao.getEmptyMileageByCadence(cadenceId) ?: 0L

        // ← ИСПРАВЛЕНО: убран лишний импорт, явный тип переменной
        val rawStats: List<RouteWeightStatistics> = routeDao.getMileageByWeightCategory(cadenceId)
        val weightCategories = rawStats.map { it.toDomain() }

        return MileageStats(
            totalMileage = total,
            loadedMileage = loaded,
            emptyMileage = empty,
            loadedPercentage = if (total > 0) loaded * 100.0 / total else 0.0,
            emptyPercentage = if (total > 0) empty * 100.0 / total else 0.0,
            byWeightCategory = weightCategories
        )
    }

    /**
     * Только суммарный пробег (для быстрых запросов)
     */
    suspend fun getTotalMileage(cadenceId: Long): Long {
        return routeDao.getTotalMileageByCadence(cadenceId) ?: 0L
    }

    /**
     * Пробег с/без груза (для сводки)
     */
    suspend fun getLoadedVsEmpty(cadenceId: Long): Pair<Long, Long> {
        val loaded = routeDao.getLoadedMileageByCadence(cadenceId) ?: 0L
        val empty = routeDao.getEmptyMileageByCadence(cadenceId) ?: 0L
        return loaded to empty
    }
}