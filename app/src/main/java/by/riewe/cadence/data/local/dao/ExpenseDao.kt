package by.riewe.cadence.data.local.dao

import androidx.room.*
import by.riewe.cadence.data.local.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE cadenceId = :cadenceId ORDER BY date ASC")
    fun getExpensesByCadence(cadenceId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE cadenceId = :cadenceId ORDER BY date ASC")
    suspend fun getExpensesByCadenceList(cadenceId: Long): List<ExpenseEntity>

    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Transaction
    suspend fun reorderExpenses(cadenceId: Long) {
        val expenses = getExpensesByCadenceList(cadenceId)
        expenses.forEachIndexed { index, expense ->
            val expectedNumber = index + 1
            if (expense.expenseNumber != expectedNumber) {
                update(expense.copy(expenseNumber = expectedNumber))
            }
        }
    }

    @Transaction
    suspend fun deleteAndReorder(expense: ExpenseEntity) {
        val cadenceId = expense.cadenceId
        delete(expense)
        reorderExpenses(cadenceId)
    }

    @Query("SELECT MAX(expenseNumber) FROM expenses WHERE cadenceId = :cadenceId")
    suspend fun getMaxExpenseNumber(cadenceId: Long): Int?

    @Query("SELECT SUM(amount) FROM expenses WHERE cadenceId = :cadenceId")
    suspend fun getTotalExpensesByCadence(cadenceId: Long): Double?
}