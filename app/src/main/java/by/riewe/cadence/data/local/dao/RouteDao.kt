package by.riewe.cadence.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.model.RouteWeightStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    // ============================================================
    // ПОЛУЧЕНИЕ ДАННЫХ
    // ============================================================

    @Query("SELECT * FROM routes WHERE cadenceId = :cadenceId ORDER BY routeNumber")
    fun getRoutesByCadence(cadenceId: Long): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE cadenceId = :cadenceId ORDER BY routeNumber")
    suspend fun getRoutesByCadenceList(cadenceId: Long): List<RouteEntity>

    @Query("SELECT * FROM routes WHERE cadenceId = :cadenceId AND isActive = 1 LIMIT 1")
    suspend fun getActiveRoute(cadenceId: Long): RouteEntity?

    @Query("SELECT * FROM routes WHERE cadenceId = :cadenceId AND isActive = 1 LIMIT 1")
    suspend fun getActiveRouteByCadence(cadenceId: Long): RouteEntity?

    @Query("SELECT * FROM routes WHERE cadenceId = :cadenceId ORDER BY routeNumber DESC LIMIT 1")
    suspend fun getLastRouteByCadence(cadenceId: Long): RouteEntity?

    @Query("SELECT * FROM routes WHERE id = :routeId LIMIT 1")
    suspend fun getRouteById(routeId: Long): RouteEntity?

    @Query("SELECT MAX(routeNumber) FROM routes WHERE cadenceId = :cadenceId")
    suspend fun getMaxRouteNumber(cadenceId: Long): Int?

    // ============================================================
    // ВСТАВКА И ОБНОВЛЕНИЕ
    // ============================================================

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteEntity): Long

    @Update
    suspend fun update(route: RouteEntity)

    // ============================================================
    // ЗАКРЫТИЕ МАРШРУТА С РАСЧЁТОМ
    // ============================================================

    @Query("""
    UPDATE routes 
    SET endLocation = :endLocation,
        endOdometer = :endOdometer,
        endEngineHours = :endEngineHours,
        mileAge = :mileAge,
        totalEngineHours = :totalEngineHours,
        fuelConsumption = :fuelConsumption,
        fuelBurned = :fuelBurned,
        isActive = 0 
    WHERE id = :routeId
""")
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

    // ============================================================
    // ОБЩИЙ ПРОБЕГ (все маршруты)
    // ============================================================

    @Query("SELECT SUM(mileAge) FROM routes WHERE cadenceId = :cadenceId")
    suspend fun getTotalMileageByCadence(cadenceId: Long): Long?

    // ============================================================
    // ПРОБЕГ С ГРУЗОМ (weight > 0)
    // ============================================================

    /**
     * Пробег с грузом по каденции
     */
    @Query("""
        SELECT SUM(mileAge) 
        FROM routes 
        WHERE cadenceId = :cadenceId 
          AND weight IS NOT NULL 
          AND weight > 0
    """)
    suspend fun getLoadedMileageByCadence(cadenceId: Long): Long?

    // ============================================================
    // ПРОБЕГ БЕЗ ГРУЗА (weight = 0 OR weight IS NULL)
    // ============================================================

    /**
     * Пробег без груза по каденции
     */
    @Query("""
        SELECT SUM(mileAge) 
        FROM routes 
        WHERE cadenceId = :cadenceId 
          AND (weight IS NULL OR weight = 0)
    """)
    suspend fun getEmptyMileageByCadence(cadenceId: Long): Long?

    // ============================================================
    // СТАТИСТИКА ПО ГРУЗОПОДЪЁМНОСТИ (группировка по весу)
    // ============================================================

    /**
     * Пробег по категориям веса для периода
     * Возвращает: весовая категория, количество рейсов, суммарный пробег
     */
    @Query("""
        SELECT 
            CASE 
                WHEN weight IS NULL OR weight = 0 THEN 'empty'
                WHEN weight <= 10 THEN 'light'
                WHEN weight <= 20 THEN 'medium'
                ELSE 'heavy'
            END as weightCategory,
            COUNT(*) as routeCount,
            SUM(mileAge) as totalMileage
        FROM routes 
        WHERE cadenceId = :cadenceId AND mileAge IS NOT NULL
        GROUP BY weightCategory
    """)
    suspend fun getMileageByWeightCategory(cadenceId: Long): List<RouteWeightStatistics>


    /**
     * Средний вес груза по периоду (только рейсы с грузом)
     */
    @Query("""
        SELECT AVG(weight) 
        FROM routes 
        WHERE cadenceId = :cadenceId 
          AND weight IS NOT NULL 
          AND weight > 0
    """)
    suspend fun getAverageWeight(cadenceId: Long): Double?

    // ============================================================
    // ТОПЛИВНАЯ СТАТИСТИКА
    // ============================================================

    @Query("SELECT SUM(fuelBurned) FROM routes WHERE cadenceId = :cadenceId")
    suspend fun getTotalFuelBurnedByCadence(cadenceId: Long): Double?

    /**
     * Средний расход на 100 км по периоду
     */
    @Query("""
        SELECT 
            CASE 
                WHEN SUM(mileAge) > 0 THEN (SUM(fuelBurned) * 100.0 / SUM(mileAge))
                ELSE NULL 
            END 
        FROM routes 
        WHERE cadenceId = :cadenceId AND fuelBurned IS NOT NULL
    """)
    suspend fun getAverageFuelConsumption(cadenceId: Long): Double?

    // ============================================================
    // УДАЛЕНИЕ
    // ============================================================

    @Transaction
    suspend fun reorderRoutes(cadenceId: Long) {
        val routes = getRoutesByCadenceList(cadenceId)
        routes.forEachIndexed { index, route ->
            val expectedNumber = index + 1
            if (route.routeNumber != expectedNumber) {
                update(route.copy(routeNumber = expectedNumber))
            }
        }
    }

    @Transaction
    suspend fun deleteAndReorder(routeId: Long) {
        val route = getRouteById(routeId) ?: return
        val cadenceId = route.cadenceId
        deleteRouteById(routeId)
        reorderRoutes(cadenceId)
    }

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: Long)

    @Query("DELETE FROM routes WHERE cadenceId = :cadenceId")
    suspend fun deleteRoutesByCadence(cadenceId: Long)
}
