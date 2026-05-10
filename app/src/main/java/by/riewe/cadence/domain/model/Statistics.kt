package by.riewe.cadence.domain.model

// ============================================
// ВСЕ СТАТИСТИЧЕСКИЕ МОДЕЛИ В ОДНОМ ФАЙЛЕ
// ============================================

/**
 * Полная статистика пробега маршрутов
 */
data class MileageStats(
    val totalMileage: Long = 0,
    val loadedMileage: Long = 0,      // С грузом (weight > 0)
    val emptyMileage: Long = 0,        // Без груза
    val loadedPercentage: Double = 0.0,
    val emptyPercentage: Double = 0.0,
    val byWeightCategory: List<WeightCategory> = emptyList()
)

/**
 * Статистика по категории веса
 */
data class WeightCategory(
    val type: WeightCategoryType,
    val routeCount: Int,
    val totalMileage: Long
)

enum class WeightCategoryType {
    EMPTY,      // Без груза
    LIGHT,      // До 10 т
    MEDIUM,     // 10-20 т
    HEAVY       // Свыше 20 т
}

/**
 * Расширение для отображения
 */
fun WeightCategoryType.displayName(): String = when (this) {
    WeightCategoryType.EMPTY -> "Пустой"
    WeightCategoryType.LIGHT -> "До 10т"
    WeightCategoryType.MEDIUM -> "10-20т"
    WeightCategoryType.HEAVY -> "Свыше 20т"
}