package by.riewe.cadence.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.TruckSettingsEntity
import by.riewe.cadence.domain.repository.TruckSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TruckSettingsViewModel @Inject constructor(
    private val repository: TruckSettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow<TruckSettingsEntity?>(null)
    val settings: StateFlow<TruckSettingsEntity?> = _settings.asStateFlow()

    fun loadSettings(truckNumber: String) {
        viewModelScope.launch {
            repository.getSettingsForTruck(truckNumber).collect {
                _settings.value = it ?: TruckSettingsEntity(truckNumber)
            }
        }
    }

    fun updateSettings(truckNumber: String, baseConsumption: Double, weightCoefficient: Double) {
        viewModelScope.launch {
            val newSettings = TruckSettingsEntity(
                truckNumber = truckNumber,
                baseConsumption = baseConsumption,
                weightCoefficient = weightCoefficient
            )
            repository.saveSettings(newSettings)
        }
    }
}
