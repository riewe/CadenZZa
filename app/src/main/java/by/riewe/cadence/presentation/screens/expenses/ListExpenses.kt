package by.riewe.cadence.presentation.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.presentation.common.components.ExpenseCard
import by.riewe.cadence.presentation.screens.cadence.EmptyTabContent
import by.riewe.cadence.presentation.theme.CadenceTheme

@Composable
fun ListExpenses(
    expenses: List<ExpenseEntity>,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    val sortedExpenses = remember(expenses) { expenses.sortedByDescending { it.expenseNumber } }

    if (sortedExpenses.isEmpty()) {
        EmptyTabContent(Icons.Default.ShoppingCart, "Нет расходов")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedExpenses, key = { "expense_${it.id}" }) { expense ->
                ExpenseCard(
                    expense = expense,
                    onDelete = { onDeleteExpense(expense) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListExpensesPreview() {
    val expenses = listOf(
        ExpenseEntity(
            id = 1,
            cadenceId = 1,
            expenseNumber = 1,
            date = System.currentTimeMillis(),
            location = "PL",
            cardName = "DKV",
            amount = 25.0,
            currency = "EUR",
            description = "Parking"
        )
    )
    CadenceTheme {
        ListExpenses(expenses = expenses, onDeleteExpense = {})
    }
}