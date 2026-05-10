package by.riewe.cadence.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cadenceId: Long,
    val expenseNumber: Int,
    val date: Long,
    val location: String,
    val cardName: String,
    val amount: Double,
    val currency: String,
    val description: String?
)