package by.riewe.cadence.domain.model

data class TrailerHistoryItem(
    val trailerNumber: String,
    val receivedFrom: String,           // От какого тягача получили
    val startDate: Long,
    val endDate: Long?,
    val startFuel: Double,              // При получении
    val endFuel: Double?,               // При сдаче (если сдан)
    val totalRefueled: Double,          // Сколько залили за время работы
    val refuelCount: Int,               // Количество заправок
    val isActive: Boolean               // Текущий активный прицеп
)


data class RefuelRecommendation(
    val trailerNumber: String,
    val currentFuelLevel: Double,       // Расчётный остаток
    val recommendedRefuel: Double,      // Рекомендуемое количество
    val isLowFuel: Boolean              // Критический остаток
)


