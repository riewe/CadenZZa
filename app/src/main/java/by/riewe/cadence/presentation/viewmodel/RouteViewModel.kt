package by.riewe.cadence.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.RouteRepository
import by.riewe.cadence.presentation.common.components.IsDateWithinCadence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val cadenceRepository: CadenceRepository
) : ViewModel() {

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    private val _route = MutableStateFlow<RouteEntity?>(null)
    val route: StateFlow<RouteEntity?> = _route.asStateFlow()

    fun getRoutes(cadenceId: Long): Flow<List<RouteEntity>> {
        viewModelScope.launch {
            routeRepository.reorderRoutes(cadenceId)
        }
        return routeRepository.getRoutesByCadence(cadenceId)
    }

    fun loadRoute(routeId: Long) {
        viewModelScope.launch {
            _route.value = routeRepository.getRouteById(routeId)
        }
    }

    fun deleteRoute(routeId: Long) {
        viewModelScope.launch {
            routeRepository.deleteAndReorderRoutes(routeId)
        }
    }

    suspend fun getNextRouteNumber(cadenceId: Long): Int {
        return routeRepository.getNextRouteNumber(cadenceId)
    }

    fun saveRoute(
        routeId: Long = 0,
        cadenceId: Long,
        startDate: Date,
        startLocation: String,
        startOdometer: Int,
        startEngineHours: Int,
        goodsDescription: String,
        weight: Double?,
        cmrNumber: String,
        refrigeMode: String,
        temperatureValue: String,
        trailerNumber: String?,
        endDate: Date? = null,
        endLocation: String? = null,
        endOdometer: Int? = null,
        endEngineHours: Int? = null,
        fuelBurned: Double? = null,
        fuelConsumption: Double? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = if (routeId > 0) routeRepository.getRouteById(routeId) else null
            val finalCadenceId = if (cadenceId > 0) cadenceId else existing?.cadenceId ?: -1L

            val cadence = cadenceRepository.getCadenceById(finalCadenceId).firstOrNull()
            if (cadence == null) {
                error = "Каденция не найдена"
                return@launch
            }

            if (!IsDateWithinCadence(startDate, cadence)) {
                error = "Дата начала рейса не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            if (endDate != null) {
                if (!IsDateWithinCadence(endDate, cadence)) {
                    error = "Дата окончания рейса не попадает в период каденции №${cadence.cadenceNumber}."
                    return@launch
                }
                if (endDate.before(startDate)) {
                    error = "Дата окончания рейса не может быть раньше даты его начала."
                    return@launch
                }
            }

            val finalRouteNumber = existing?.routeNumber ?: routeRepository.getNextRouteNumber(finalCadenceId)
            
            val isFinished = endOdometer != null && endOdometer > 0
            val mileAge = if (isFinished) (endOdometer - startOdometer).coerceAtLeast(0) else null
            
            val isEmptyCargo = goodsDescription.trim().lowercase() == "пустой" || 
                              goodsDescription.trim().lowercase() == "empty"
            val finalCmrNumber = if (isEmptyCargo) "" else cmrNumber

            val consumption = fuelConsumption ?: if (isFinished && mileAge != null && mileAge > 0 && fuelBurned != null) {
                (fuelBurned * 100.0 / mileAge)
            } else null

            val totalHours = if (isFinished && endEngineHours != null) (endEngineHours - startEngineHours).coerceAtLeast(0) else null

            val routeToSave = RouteEntity(
                id = if (routeId > 0) routeId else 0,
                cadenceId = finalCadenceId,
                routeNumber = finalRouteNumber,
                startLocation = startLocation,
                startDate = startDate,
                startOdometer = startOdometer,
                startEngineHours = startEngineHours,
                goodsDescription = goodsDescription,
                weight = weight,
                cmrNumber = cmrNumber,
                agregateMode = refrigeMode,
                temperatureValue = temperatureValue,
                trailerNumber = trailerNumber,
                endDate = if (isFinished) endDate else null,
                endLocation = if (isFinished) endLocation else null,
                endOdometer = if (isFinished) endOdometer else null,
                endEngineHours = if (isFinished) endEngineHours else null,
                fuelBurned = if (isFinished) fuelBurned else null,
                fuelConsumption = consumption,
                isActive = !isFinished,
                mileAge = mileAge,
                totalEngineHours = totalHours
            )
            
            val wasActive = existing?.isActive ?: true
            
            if (routeId > 0) {
                routeRepository.updateRoute(routeToSave)
            } else {
                routeRepository.addRoute(routeToSave)
            }

            // Создаем следующий рейс только если этот был только что завершен (был активен и стал завершен)
            if (wasActive && isFinished) {
                val nextRouteNumber = routeRepository.getNextRouteNumber(finalCadenceId)
                val nextRoute = RouteEntity(
                    cadenceId = finalCadenceId,
                    routeNumber = nextRouteNumber,
                    startLocation = endLocation ?: startLocation,
                    startDate = endDate ?: startDate,
                    startOdometer = endOdometer ?: startOdometer,
                    startEngineHours = endEngineHours ?: startEngineHours,
                    goodsDescription = "Продолжение маршрута",
                    trailerNumber = trailerNumber,
                    isActive = true
                )
                routeRepository.addRoute(nextRoute)
            }

            onSuccess()
        }
    }

    fun closeRoute(
        routeId: Long,
        endDate: Date,
        endLocation: String,
        endOdometer: Int,
        endEngineHours: Int,
        fuelBurned: Double?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentRoute = routeRepository.getRouteById(routeId) ?: return@launch
            if (!currentRoute.isActive) {
                onSuccess()
                return@launch
            }

            val cadence = cadenceRepository.getCadenceById(currentRoute.cadenceId).firstOrNull()
            if (cadence != null && !IsDateWithinCadence(endDate, cadence)) {
                error = "Дата завершения рейса не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            if (endDate.before(currentRoute.startDate)) {
                error = "Дата завершения рейса не может быть раньше даты его начала."
                return@launch
            }
            
            val mileage = (endOdometer - currentRoute.startOdometer).coerceAtLeast(0)
            val totalHours = (endEngineHours - currentRoute.startEngineHours).coerceAtLeast(0)
            
            val consumption = if (mileage > 0 && fuelBurned != null) {
                (fuelBurned * 100.0 / mileage)
            } else null

            val updatedRoute = currentRoute.copy(
                endLocation = endLocation,
                endDate = endDate,
                endOdometer = endOdometer,
                endEngineHours = endEngineHours,
                mileAge = mileage,
                totalEngineHours = totalHours,
                fuelBurned = fuelBurned,
                fuelConsumption = consumption,
                isActive = false
            )
            routeRepository.updateRoute(updatedRoute)
            
            // Автоматическое создание следующего рейса
            val nextRouteNumber = routeRepository.getNextRouteNumber(currentRoute.cadenceId)
            val nextRoute = RouteEntity(
                cadenceId = currentRoute.cadenceId,
                routeNumber = nextRouteNumber,
                startLocation = endLocation,
                startDate = endDate,
                startOdometer = endOdometer,
                startEngineHours = endEngineHours,
                goodsDescription = "Продолжение маршрута",
                trailerNumber = currentRoute.trailerNumber,
                isActive = true
            )
            routeRepository.addRoute(nextRoute)
            
            onSuccess()
        }
    }

    /**
     * Получает данные для инициализации нового рейса на основе последнего завершенного.
     */
    suspend fun getInitialRouteData(cadenceId: Long): RouteEntity? {
        return routeRepository.getLastRouteByCadence(cadenceId)
    }
}
