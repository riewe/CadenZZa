package by.riewe.cadence.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.TrailerChangeRepository
import by.riewe.cadence.presentation.common.components.IsDateWithinCadence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TrailerChangeViewModel @Inject constructor(
    private val trailerChangeRepository: TrailerChangeRepository,
    private val cadenceRepository: CadenceRepository
) : ViewModel() {

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    private val _activeChange = MutableStateFlow<TrailerChangeEntity?>(null)
    val activeChange: StateFlow<TrailerChangeEntity?> = _activeChange.asStateFlow()

    private val _selectedChange = MutableStateFlow<TrailerChangeEntity?>(null)
    val selectedChange: StateFlow<TrailerChangeEntity?> = _selectedChange.asStateFlow()

    fun loadActiveChange(cadenceId: Long) {
        viewModelScope.launch {
            _activeChange.value = trailerChangeRepository.getActiveChange(cadenceId)
        }
    }

    fun loadChange(changeId: Long) {
        viewModelScope.launch {
            _selectedChange.value = trailerChangeRepository.getChangeById(changeId)
        }
    }

    fun getChanges(cadenceId: Long): Flow<List<TrailerChangeEntity>> {
        viewModelScope.launch {
            trailerChangeRepository.reorderChanges(cadenceId)
        }
        return trailerChangeRepository.getChangesByCadence(cadenceId)
    }

    fun performTrailerChange(
        cadenceId: Long,
        date: Long,
        location: String,
        oldTrailerFuel: Int,
        oldTrailerHours: Int,
        donorTruckNumber: String,
        newTrailerNumber: String,
        newTrailerFuel: Int,
        newTrailerHours: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cadence = cadenceRepository.getCadenceById(cadenceId).firstOrNull()
            if (cadence != null && !IsDateWithinCadence(date, cadence)) {
                error = "Дата перецепа не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            // 1. Закрываем текущую (отдаем)
            val currentActive = trailerChangeRepository.getActiveChange(cadenceId)
            if (currentActive != null) {
                if (date < currentActive.startDate) {
                    error = "Дата перецепа не может быть раньше даты принятия текущего прицепа."
                    return@launch
                }
                trailerChangeRepository.closeTrailerChange(
                    changeId = currentActive.id,
                    endDate = Date(date),
                    endTrailerFuel = oldTrailerFuel,
                    endEngineHours = oldTrailerHours,
                    endLocation = location
                )
            }

            // 2. Создаем новую (принимаем)
            val nextNumber = trailerChangeRepository.getNextChangeNumber(cadenceId)
            val newChange = TrailerChangeEntity(
                cadenceId = cadenceId,
                changeNumber = nextNumber,
                startDate = date,
                trailerNumber = newTrailerNumber,
                donorTruckNumber = donorTruckNumber,
                startTrailerFuel = newTrailerFuel,
                startEngineHours = newTrailerHours,
                startLocation = location,
                isActive = true
            )
            trailerChangeRepository.addTrailerChange(newChange)
            
            // 3. Обновляем локальное состояние активного прицепа
            _activeChange.value = newChange

            onSuccess()
        }
    }

    fun closeTrailerChange(
        changeId: Long,
        endDate: Date,
        endTrailerFuel: Int?,
        endEngineHours: Int?,
        endLocation: String?,
        cadenceId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cadence = cadenceRepository.getCadenceById(cadenceId).firstOrNull()
            if (cadence != null && !IsDateWithinCadence(endDate, cadence)) {
                error = "Дата закрытия перецепа не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            val currentChange = trailerChangeRepository.getChangeById(changeId)
            if (currentChange != null && endDate.time < currentChange.startDate) {
                error = "Дата закрытия перецепа не может быть раньше даты его начала."
                return@launch
            }

            trailerChangeRepository.closeTrailerChange(
                changeId = changeId,
                endDate = endDate,
                endTrailerFuel = endTrailerFuel,
                endEngineHours = endEngineHours,
                endLocation = endLocation
            )
            trailerChangeRepository.reorderChanges(cadenceId)
            onSuccess()
        }
    }

    fun deleteTrailerChange(change: TrailerChangeEntity) {
        viewModelScope.launch {
            trailerChangeRepository.deleteTrailerChange(change)
            trailerChangeRepository.reorderChanges(change.cadenceId)
        }
    }

    fun updateTrailerChange(
        changeId: Long,
        date: Long,
        location: String,
        trailerNumber: String,
        donorTruckNumber: String,
        trailerFuel: Int,
        engineHours: Int,
        isActive: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = trailerChangeRepository.getChangeById(changeId) ?: return@launch
            
            // Если это активный перецеп (или начальный), мы просто обновляем его данные
            val updated = existing.copy(
                startDate = date,
                startLocation = location,
                trailerNumber = trailerNumber,
                donorTruckNumber = donorTruckNumber,
                startTrailerFuel = trailerFuel,
                startEngineHours = engineHours,
                isActive = isActive
            )
            
            trailerChangeRepository.updateTrailerChange(updated)
            onSuccess()
        }
    }
}
