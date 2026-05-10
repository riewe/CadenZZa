package by.riewe.cadence.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.domain.model.DetailedFuelBalance
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.RefuelRepository
import by.riewe.cadence.presentation.common.components.IsDateWithinCadence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val refuelRepository: RefuelRepository,
    private val cadenceRepository: CadenceRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    var fuelBalance by mutableStateOf<DetailedFuelBalance?>(null)
        private set

    fun getRefuels(cadenceId: Long): Flow<List<RefuelEntity>> {
        viewModelScope.launch {
            refuelRepository.reorderRefuels(cadenceId)
        }
        return refuelRepository.getRefuelsByCadence(cadenceId)
    }

    fun deleteRefuel(refuel: RefuelEntity) {
        viewModelScope.launch {
            refuelRepository.deleteRefuel(refuel.id)
            refuelRepository.reorderRefuels(refuel.cadenceId)
            loadFuelBalance(refuel.cadenceId)
        }
    }

    fun loadFuelBalance(cadenceId: Long) {
        viewModelScope.launch {
            isLoading = true
            val cadence = cadenceRepository.getCadenceById(cadenceId).firstOrNull()
            if (cadence != null) {
                fuelBalance = refuelRepository.getDetailedFuelBalance(cadenceId, cadence.initialTruckFuel)
            }
            isLoading = false
        }
    }

    fun addRefuel(
        cadenceId: Long,
        date: Date,
        location: String,
        truckFuel: Int,
        adBlue: Int,
        trailerFuel: Int,
        cardName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cadence = cadenceRepository.getCadenceById(cadenceId).firstOrNull()
            if (cadence != null && !IsDateWithinCadence(date, cadence)) {
                error = "Дата заправки не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            refuelRepository.addRefuel(
                cadenceId = cadenceId,
                date = date,
                location = location,
                truckFuel = truckFuel,
                adBlue = adBlue,
                trailerFuel = trailerFuel,
                cardName = cardName
            )
            loadFuelBalance(cadenceId)
            onSuccess()
        }
    }
}
