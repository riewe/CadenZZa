package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.RefuelDao
import by.riewe.cadence.data.local.dao.TrailerChangeDao
import by.riewe.cadence.data.local.dao.RouteDao
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.model.*
import by.riewe.cadence.domain.repository.RefuelRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

/**
 * Реализация репозитория для работы с заправками.
 */
class RefuelRepositoryImpl @Inject constructor(
    private val refuelDao: RefuelDao,
    private val trailerChangeDao: TrailerChangeDao,
    private val routeDao: RouteDao
) : RefuelRepository {

    override fun getRefuelsByCadence(cadenceId: Long): Flow<List<RefuelEntity>> {
        return refuelDao.getRefuelsByCadence(cadenceId)
    }

    /**
     * Добавляет заправку, автоматически определяя прицеп по времени события.
     */
    override suspend fun addRefuel(
        cadenceId: Long,
        date: Date,
        location: String,
        truckFuel: Int,
        adBlue: Int,
        trailerFuel: Int,
        cardName: String
    ): Long {
        // Определяем прицеп на момент заправки
        val currentTrailer = trailerChangeDao.getChangeAtMoment(cadenceId, date.time)
        val nextNumber = (refuelDao.getMaxRefuelNumber(cadenceId) ?: 0) + 1

        val refuel = RefuelEntity(
            cadenceId = cadenceId,
            refuelNumber = nextNumber,
            date = date,
            location = location,
            truckFuel = truckFuel,
            adBlue = adBlue,
            trailerFuel = trailerFuel,
            trailerNumber = currentTrailer?.trailerNumber,
            cardName = cardName
        )
        return refuelDao.insert(refuel)
    }

    override suspend fun canRefuelTrailer(cadenceId: Long): Boolean {
        val activeChange = trailerChangeDao.getActiveChange(cadenceId)
        return activeChange != null && activeChange.isActive
    }

    override suspend fun getDetailedFuelBalance(
        cadenceId: Long,
        initialTruckFuel: Int
    ): DetailedFuelBalance {

        // 1. Тягач
        val truckRefueled = refuelDao.getTotalTruckFuelByCadence(cadenceId) ?: 0
        val truckConsumed = routeDao.getTotalFuelBurnedByCadence(cadenceId)?.toInt() ?: 0

        // 2. Прицепы
        val changes = trailerChangeDao.getChangesByCadenceOrdered(cadenceId)

        val trailerBalances = changes.map { change: TrailerChangeEntity ->
            val refueled = refuelDao.getTrailerFuelByCadence(cadenceId, change.trailerNumber) ?: 0

            val initial = change.startTrailerFuel
            val remaining = change.endTrailerFuel ?: (initial + refueled)

            TrailerBalance(
                number = change.trailerNumber,
                initial = initial,
                refueled = refueled,
                remaining = remaining
            )
        }

        // 3. Текущий баланс и список прицепов
        val currentBal = trailerBalances.find { bal ->
            changes.any { it.trailerNumber == bal.number && it.isActive }
        } ?: trailerBalances.lastOrNull() ?: TrailerBalance("N/A", 0, 0, 0)

        val simpleTrailerInfo = trailerBalances.map {
            SimpleTrailerInfo(
                number = it.number,
                totalRefueled = it.refueled,
                refuelCount = 0
            )
        }

        return DetailedFuelBalance(
            truck = VehicleBalance(
                initial = initialTruckFuel,
                refueled = truckRefueled,
                remaining = initialTruckFuel + truckRefueled - truckConsumed
            ),
            currentTrailer = currentBal,
            allTrailersInCadence = simpleTrailerInfo
        )
    }

    override suspend fun deleteRefuel(refuelId: Long) {
        refuelDao.deleteAndReorder(refuelId)
    }

    override suspend fun reorderRefuels(cadenceId: Long) {
        refuelDao.reorderRefuels(cadenceId)
    }
}
