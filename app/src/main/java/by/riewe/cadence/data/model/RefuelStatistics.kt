package by.riewe.cadence.data.model

/**
 * Статистика заправок по прицепу в периоде
 */
data class TrailerRefuelStat(
    val trailerNumber: String,
    val refuelCount: Int,
    val totalFuel: Int?,
    val firstRefuel: Long,
    val lastRefuel: Long
)

/**
 * Статистика прицепа за каденцию
 */
data class TrailerCadenceStat(
    val trailerNumber: String,
    val totalFuel: Int?,
    val refuelCount: Int
)

/**
 * Детальная статистика по карте и прицепу
 */
data class DetailedCardStat(
    val cardName: String,
    val trailerNumber: String?,
    val refuelCount: Int,
    val totalTruckFuel: Int?,
    val totalAdBlue: Int?,
    val totalTrailerFuel: Int?
)

/**
 * Статистика заправок по карте (без разбивки по прицепам)
 */
data class RefuelCardStats(
    val cardName: String,
    val refuelCount: Int,
    val totalTruckFuel: Int?,
    val totalAdBlue: Int?,
    val totalTrailerFuel: Int?
)

/**
 * Сводная статистика топлива
 */
data class FuelSummary(
    val totalTruckFuel: Int?,
    val totalAdBlue: Int?,
    val totalTrailerFuel: Int?,
    val totalRefuels: Int
)