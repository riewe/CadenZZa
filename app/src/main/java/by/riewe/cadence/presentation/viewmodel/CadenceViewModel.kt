package by.riewe.cadence.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.CadenceEntity
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.RouteRepository
import by.riewe.cadence.domain.repository.TrailerChangeRepository
import by.riewe.cadence.presentation.common.components.CheckDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel для управления данными каденций.
 * Отвечает за создание, загрузку и закрытие каденций.
 */
@HiltViewModel
class CadenceViewModel @Inject constructor(
    private val cadenceRepository: CadenceRepository,
    private val routeRepository: RouteRepository,
    private val trailerChangeRepository: TrailerChangeRepository
) : ViewModel() {

    /** Список всех каденций для экрана списка */
    val allCadences: StateFlow<List<CadenceEntity>> = cadenceRepository.getAllCadences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Состояние загрузки данных */
    var isLoading by mutableStateOf(false)
        private set

    /** Сообщение об ошибке, если операция не удалась */
    var error by mutableStateOf<String?>(null)
        private set

    /** Предлагаемый номер следующей каденции */
    var suggestedNumber by mutableStateOf("1")
        private set

    /** Текущая активная каденция */
    var activeCadence by mutableStateOf<CadenceEntity?>(null)
        private set

    init {
        loadSuggestedNumber()
        loadActiveCadence()
    }

    /**
     * Загружает следующий свободный номер каденции из репозитория.
     */
    private fun loadSuggestedNumber() {
        viewModelScope.launch {
            suggestedNumber = cadenceRepository.getNextCadenceNumber().toString()
        }
    }

    /**
     * Загружает текущую активную каденцию.
     */
    fun loadActiveCadence() {
        viewModelScope.launch {
            activeCadence = cadenceRepository.getActiveCadence()
        }
    }

    /**
     * Получает поток данных для конкретной каденции.
     */
    fun getCadence(cadenceId: Long): Flow<CadenceEntity?> {
        return cadenceRepository.getCadenceById(cadenceId)
    }

    /**
     * Сбрасывает текущую ошибку.
     */
    fun clearError() {
        error = null
    }

    /**
     * Создает новую каденцию и первый рейс в ней.
     */
    fun createCadence(
        data: Map<String, String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            // Проверка на наличие активной каденции
            val currentActive = cadenceRepository.getActiveCadence()
            if (currentActive != null) {
                error = "Невозможно открыть новую каденцию, так как каденция №${currentActive.cadenceNumber} еще не закрыта."
                return@launch
            }

            isLoading = true
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val startDateStr = data["startDate"] ?: throw Exception("Дата не указана")
                val parsedDate = dateFormat.parse(startDateStr) ?: throw Exception("Неверный формат даты")
                
                val startTimeStr = data["startTime"] ?: throw Exception("Время не указано")
                val timeParsed = timeFormat.parse(startTimeStr) ?: throw Exception("Неверный формат времени")
                
                val calendar = Calendar.getInstance().apply { time = timeParsed }
                val timeMillis = (calendar.get(Calendar.HOUR_OF_DAY) * 3600000L) + 
                               (calendar.get(Calendar.MINUTE) * 60000L)
                val country = data["country"] ?: "PL"

                if (CheckDate(parsedDate, allCadences.value)) {
                    error = "Выбранная дата попадает в период другой каденции. Пожалуйста, проверьте даты."
                    isLoading = false
                    return@launch
                }

                val newCadence = CadenceEntity(
                    cadenceNumber = data["cadenceNumber"]?.toIntOrNull() ?: 1,
                    driver1 = data["driver1"] ?: "",
                    driver2 = data["driver2"],
                    truckNumber = data["truckNumber"] ?: "",
                    trailerNumber = data["trailerNumber"] ?: "",
                    startDate = parsedDate,
                    startTime = timeMillis,
                    initialOdometer = data["startOdo"]?.toIntOrNull() ?: 0,
                    initialTruckFuel = data["startTruckFuel"]?.toIntOrNull() ?: 0,
                    initialTrailerFuel = data["startRefFuel"]?.toIntOrNull() ?: 0,
                    initialEngineHours = data["startMH"]?.toIntOrNull() ?: 0,
                    isActive = true
                )

                val cadenceId = cadenceRepository.startCadence(newCadence)

                // Создаем запись о начальном прицепе (перецеп №0)
                val initialTrailerChange = TrailerChangeEntity(
                    cadenceId = cadenceId,
                    changeNumber = 0,
                    startDate = parsedDate.time,
                    trailerNumber = newCadence.trailerNumber,
                    donorTruckNumber = newCadence.truckNumber,
                    startTrailerFuel = newCadence.initialTrailerFuel,
                    startEngineHours = newCadence.initialEngineHours,
                    startLocation = country,
                    isActive = true
                )
                trailerChangeRepository.addTrailerChange(initialTrailerChange)

                // Создаем первый рейс напрямую в каденции
                val firstRoute = RouteEntity(
                    cadenceId = cadenceId,
                    routeNumber = 1,
                    startLocation = country,
                    startDate = parsedDate,
                    startOdometer = newCadence.initialOdometer,
                    startEngineHours = newCadence.initialEngineHours,
                    goodsDescription = "Пустой",
                    trailerNumber = newCadence.trailerNumber,
                    isActive = true
                )
                routeRepository.addRoute(firstRoute)

                loadActiveCadence()
                onSuccess()
            } catch (e: Exception) {
                error = "Ошибка: ${e.localizedMessage ?: "неизвестная ошибка"}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Закрывает текущую каденцию.
     */
    fun closeCadence(
        data: Map<String, String>,
        onSuccess: () -> Unit
    ) {
        val cadence = activeCadence ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val parsedDate = dateFormat.parse(data["endDate"] ?: "") ?: throw Exception("Дата не указана")
                val timeParsed = timeFormat.parse(data["endTime"] ?: "00:00") ?: throw Exception("Неверный формат времени")
                
                val calendar = Calendar.getInstance().apply { time = timeParsed }
                val timeMillis = (calendar.get(Calendar.HOUR_OF_DAY) * 3600000L) + 
                               (calendar.get(Calendar.MINUTE) * 60000L)
                val country = data["country"] ?: "PL"

                if (parsedDate.before(cadence.startDate)) {
                    error = "Дата завершения каденции не может быть раньше даты её начала."
                    isLoading = false
                    return@launch
                }

                cadenceRepository.closeCadence(
                    cadenceId = cadence.id,
                    endDate = parsedDate,
                    endTime = timeMillis,
                    finalOdometer = data["endOdo"]?.toIntOrNull() ?: 0,
                    finalTruckFuel = data["endTruckFuel"]?.toIntOrNull() ?: 0,
                    finalTrailerFuel = data["endRefFuel"]?.toIntOrNull() ?: 0,
                    finalEngineHours = data["endMH"]?.toIntOrNull() ?: 0
                )

                // Закрываем текущий прицеп без создания нового перецепа
                val activeChange = trailerChangeRepository.getActiveChange(cadence.id)
                val finalOdo = data["endOdo"]?.toIntOrNull() ?: 0
                val finalMH = data["endMH"]?.toIntOrNull() ?: 0
                val finalTruckFuel = data["endTruckFuel"]?.toIntOrNull() ?: 0
                val finalTrailerFuel = data["endRefFuel"]?.toIntOrNull() ?: 0

                if (activeChange != null) {
                    val endDateTime = Calendar.getInstance().apply {
                        time = parsedDate
                        val tCal = Calendar.getInstance().apply { timeInMillis = timeMillis }
                        set(Calendar.HOUR_OF_DAY, tCal.get(Calendar.HOUR_OF_DAY))
                        set(Calendar.MINUTE, tCal.get(Calendar.MINUTE))
                    }.time

                    trailerChangeRepository.closeTrailerChange(
                        changeId = activeChange.id,
                        endDate = endDateTime,
                        endTrailerFuel = finalTrailerFuel,
                        endEngineHours = finalMH,
                        endLocation = country
                    )
                }

                // Закрываем активный рейс
                val activeRoute = routeRepository.getActiveRouteByCadence(cadence.id)
                if (activeRoute != null) {
                    val mileage = (finalOdo - activeRoute.startOdometer).coerceAtLeast(0)
                    val totalHours = (finalMH - activeRoute.startEngineHours).coerceAtLeast(0)
                    
                    // Расчет сожженного топлива тягачом для последнего рейса (упрощенно или из ввода, если бы он был)
                    // В данном случае просто закрываем рейс с финальными данными каденции
                    val updatedRoute = activeRoute.copy(
                        endLocation = country,
                        endDate = parsedDate,
                        endOdometer = finalOdo,
                        endEngineHours = finalMH,
                        mileAge = mileage,
                        totalEngineHours = totalHours,
                        isActive = false
                    )
                    routeRepository.updateRoute(updatedRoute)
                }

                activeCadence = null
                onSuccess()
            } catch (e: Exception) {
                error = "Ошибка при закрытии: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCadence(id: Long) {
        viewModelScope.launch {
            try {
                cadenceRepository.deleteCadence(id)
                if (activeCadence?.id == id) {
                    activeCadence = null
                }
            } catch (e: Exception) {
                error = "Ошибка при удалении: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Обновляет существующую каденцию.
     */
    fun updateCadence(cadenceId: Long, data: Map<String, String>, onComplete: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val parsedDate = dateFormat.parse(data["startDate"] ?: "") ?: throw Exception("Дата не указана")
                val timeParsed = timeFormat.parse(data["startTime"] ?: "00:00") ?: throw Exception("Неверный формат времени")
                
                val calendar = Calendar.getInstance().apply { time = timeParsed }
                val timeMillis = (calendar.get(Calendar.HOUR_OF_DAY) * 3600000L) + 
                               (calendar.get(Calendar.MINUTE) * 60000L)

                if (CheckDate(parsedDate, allCadences.value, ignoreId = cadenceId)) {
                    error = "Выбранная дата накладывается на другую существующую каденцию."
                    isLoading = false
                    return@launch
                }

                val current = cadenceRepository.getCadenceById(cadenceId).first() ?: throw Exception("Запись не найдена")

                val updatedCadence = current.copy(
                    cadenceNumber = data["cadenceNumber"]?.toIntOrNull() ?: current.cadenceNumber,
                    startDate = parsedDate,
                    startTime = timeMillis,
                    driver1 = data["driver1"] ?: current.driver1,
                    driver2 = data["driver2"],
                    truckNumber = data["truckNumber"] ?: current.truckNumber,
                    trailerNumber = data["trailerNumber"] ?: current.trailerNumber,
                    initialOdometer = data["startOdo"]?.toIntOrNull() ?: current.initialOdometer,
                    initialTruckFuel = data["startTruckFuel"]?.toIntOrNull() ?: current.initialTruckFuel,
                    initialTrailerFuel = data["startRefFuel"]?.toIntOrNull() ?: current.initialTrailerFuel,
                    initialEngineHours = data["startMH"]?.toIntOrNull() ?: current.initialEngineHours
                )

                cadenceRepository.updateCadence(updatedCadence)
                
                if (activeCadence?.id == cadenceId) {
                    activeCadence = updatedCadence
                }
                
                onComplete()
            } catch (e: Exception) {
                error = "Ошибка при обновлении: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
