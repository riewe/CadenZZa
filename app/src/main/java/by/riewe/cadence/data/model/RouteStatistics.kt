package by.riewe.cadence.data.local.model

import by.riewe.cadence.domain.model.WeightCategory
import by.riewe.cadence.domain.model.WeightCategoryType

// ============================================
// ВСЕ DATA-МОДЕЛИ ДЛЯ ROOM В ОДНОМ ФАЙЛЕ
// ============================================

/**
 * Промежуточный класс для Room @Query (сырые данные)
 */
data class RouteWeightStatistics(
    val weightCategory: String,  // "empty", "light", "medium", "heavy"
    val routeCount: Int,
    val totalMileage: Long?
)

/**
 * Маппинг в Domain модель
 */
fun RouteWeightStatistics.toDomain(): WeightCategory = WeightCategory(
    type = when (weightCategory) {
        "empty" -> WeightCategoryType.EMPTY
        "light" -> WeightCategoryType.LIGHT
        "medium" -> WeightCategoryType.MEDIUM
        "heavy" -> WeightCategoryType.HEAVY
        else -> WeightCategoryType.EMPTY
    },
    routeCount = routeCount,
    totalMileage = totalMileage ?: 0L
)