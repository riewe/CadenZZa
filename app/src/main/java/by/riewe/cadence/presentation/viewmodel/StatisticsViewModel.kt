package by.riewe.cadence.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.CadenceEntity
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.data.local.entities.TruckSettingsEntity
import by.riewe.cadence.domain.model.CadenceStatistics
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.ExpenseRepository
import by.riewe.cadence.domain.repository.RefuelRepository
import by.riewe.cadence.domain.repository.RouteRepository
import by.riewe.cadence.domain.repository.TrailerChangeRepository
import by.riewe.cadence.domain.repository.TruckSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val cadenceRepository: CadenceRepository,
    private val routeRepository: RouteRepository,
    private val refuelRepository: RefuelRepository,
    private val expenseRepository: ExpenseRepository,
    private val trailerChangeRepository: TrailerChangeRepository,
    private val truckSettingsRepository: TruckSettingsRepository
) : ViewModel() {

    private val _cadenceId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val statistics: StateFlow<CadenceStatistics?> = _cadenceId
        .filterNotNull()
        .flatMapLatest { id ->
            val cadenceFlow = cadenceRepository.getCadenceById(id)
            
            cadenceFlow.flatMapLatest { cadence ->
                if (cadence == null) return@flatMapLatest flowOf(null)
                
                combine(
                    routeRepository.getRoutesByCadence(id),
                    refuelRepository.getRefuelsByCadence(id),
                    expenseRepository.getExpensesByCadence(id),
                    trailerChangeRepository.getChangesByCadence(id),
                    truckSettingsRepository.getSettingsForTruck(cadence.truckNumber)
                ) { routes, refuels, expenses, changes, settings ->
                    calculateStats(
                        cadence,
                        routes,
                        refuels,
                        expenses,
                        changes,
                        settings ?: TruckSettingsEntity(cadence.truckNumber)
                    )
                }
            }
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun loadStatistics(cadenceId: Long) {
        _cadenceId.value = cadenceId
    }

    /**
     * Обратная совместимость для существующего кода
     */
    fun getStatistics(cadenceId: Long): Flow<CadenceStatistics?> {
        loadStatistics(cadenceId)
        return statistics
    }

    private fun calculateStats(
        cadence: CadenceEntity,
        routes: List<RouteEntity>,
        refuels: List<RefuelEntity>,
        expenses: List<ExpenseEntity>,
        changes: List<TrailerChangeEntity>,
        settings: TruckSettingsEntity
    ): CadenceStatistics {
        var totalMileage = 0
        var totalFuelNormal = 0.0
        var totalWeightRatio = 0.0
        var totalWeight = 0.0

        routes.forEach { route ->
            val mileage = route.mileAge ?: 0
            val weight = route.weight ?: 0.0
            
            totalMileage += mileage
            totalWeight += weight
            totalWeightRatio += (weight * mileage)

            val routeNormal = (mileage.toDouble() * (settings.baseConsumption + weight * settings.weightCoefficient)) / 100.0
            totalFuelNormal += routeNormal
        }

        val totalRefueled = refuels.sumOf { it.truckFuel }
        val currentFuelLevel = cadence.initialTruckFuel.toDouble() + totalRefueled - totalFuelNormal
        
        val averageWeight = if (totalMileage > 0) totalWeightRatio / totalMileage else 0.0
        val averageConsumptionNormal = if (totalMileage > 0) (totalFuelNormal / totalMileage) * 100 else settings.baseConsumption

        val totalExpenses = expenses.sumOf { it.amount }
        val totalTrailerEngineHours = changes.sumOf { it.totalEngineHours ?: 0 }

        return CadenceStatistics(
            totalMileage = totalMileage,
            totalFuelNormal = totalFuelNormal,
            averageConsumptionNormal = averageConsumptionNormal,
            totalRefueled = totalRefueled,
            currentFuelLevel = currentFuelLevel,
            totalWeight = totalWeight,
            averageWeight = averageWeight,
            totalExpenses = totalExpenses,
            totalTrailerEngineHours = totalTrailerEngineHours
        )
    }
}
