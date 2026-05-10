package by.riewe.cadence.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.domain.repository.CadenceRepository
import by.riewe.cadence.domain.repository.ExpenseRepository
import by.riewe.cadence.presentation.common.components.IsDateWithinCadence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val cadenceRepository: CadenceRepository
) : ViewModel() {

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    fun getExpenses(cadenceId: Long): Flow<List<ExpenseEntity>> {
        viewModelScope.launch {
            expenseRepository.reorderExpenses(cadenceId)
        }
        return expenseRepository.getExpensesByCadence(cadenceId)
    }

    fun addExpense(
        cadenceId: Long,
        date: Long,
        location: String,
        cardName: String,
        amount: Double,
        currency: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cadence = cadenceRepository.getCadenceById(cadenceId).firstOrNull()
            if (cadence != null && !IsDateWithinCadence(date, cadence)) {
                error = "Дата расхода не попадает в период каденции №${cadence.cadenceNumber}."
                return@launch
            }

            val nextNumber = expenseRepository.getNextExpenseNumber(cadenceId)
            expenseRepository.addExpense(
                cadenceId = cadenceId,
                expenseNumber = nextNumber,
                date = date,
                location = location,
                cardName = cardName,
                amount = amount,
                currency = currency,
                description = description
            )
            onSuccess()
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
            expenseRepository.reorderExpenses(expense.cadenceId)
        }
    }
}
