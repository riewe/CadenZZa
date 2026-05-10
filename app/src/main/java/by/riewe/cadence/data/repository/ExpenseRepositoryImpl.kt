package by.riewe.cadence.data.repository

import by.riewe.cadence.data.local.dao.ExpenseDao
import by.riewe.cadence.data.local.dao.TrailerChangeDao
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val trailerChangeDao: TrailerChangeDao
) : ExpenseRepository {

    override fun getExpensesByCadence(cadenceId: Long): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCadence(cadenceId)
    }

    override suspend fun getMaxExpenseNumber(cadenceId: Long): Int? {
        return expenseDao.getMaxExpenseNumber(cadenceId)
    }

    override suspend fun getNextExpenseNumber(cadenceId: Long): Int {
        val lastNumber = expenseDao.getMaxExpenseNumber(cadenceId) ?: 0
        return lastNumber + 1
    }

    override suspend fun addExpense(
        cadenceId: Long,
        expenseNumber: Int,
        date: Long,
        location: String,
        cardName: String,
        amount: Double,
        currency: String,
        description: String
    ): Long {
        var finalDescription = description

        // Логика: если "Мойка прицепа" и карта "TRAVIS"
        if (description.contains("Мойка прицепа", ignoreCase = true) &&
            cardName.equals("TRAVIS", ignoreCase = true)) {

            // Ищем активный прицеп на момент этой даты
            val activeTrailer = trailerChangeDao.getChangeAtMoment(cadenceId, date)
            activeTrailer?.let {
                finalDescription = "Мойка прицепа ${it.trailerNumber}"
            }
        }

        val expense = ExpenseEntity(
            cadenceId = cadenceId,
            expenseNumber = expenseNumber,
            date = date,
            location = location,
            cardName = cardName,
            amount = amount,
            currency = currency,
            description = finalDescription
        )

        return expenseDao.insert(expense)
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteAndReorder(expense)
    }

    override suspend fun reorderExpenses(cadenceId: Long) {
        expenseDao.reorderExpenses(cadenceId)
    }
}