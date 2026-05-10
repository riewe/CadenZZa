package by.riewe.cadence.domain.repository

import by.riewe.cadence.data.local.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesByCadence(cadenceId: Long): Flow<List<ExpenseEntity>>
    suspend fun getMaxExpenseNumber(cadenceId: Long): Int?
    suspend fun getNextExpenseNumber(cadenceId: Long): Int

    suspend fun addExpense(
        cadenceId: Long,
        expenseNumber: Int,
        date: Long,
        location: String,
        cardName: String,
        amount: Double,
        currency: String,
        description: String
    ): Long

    suspend fun deleteExpense(expense: ExpenseEntity)
    suspend fun reorderExpenses(cadenceId: Long)
}