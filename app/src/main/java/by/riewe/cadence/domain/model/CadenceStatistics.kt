package by.riewe.cadence.domain.model

data class CadenceStatistics(
    val totalMileage: Int,
    val totalFuelNormal: Double, // Расход по норме
    val averageConsumptionNormal: Double, // Средняя норма на 100 км
    val totalRefueled: Int,
    val currentFuelLevel: Double, // Расчетный остаток
    val totalWeight: Double, // Суммарный вес
    val averageWeight: Double, // Средний вес на км
    val totalExpenses: Double = 0.0, // Сумма расходов
    val totalTrailerEngineHours: Int = 0 // Суммарные моточасы прицепа
)
