package by.riewe.cadence.presentation.screens.refuelings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.presentation.common.components.RefuelCard
import by.riewe.cadence.presentation.screens.cadence.EmptyTabContent
import by.riewe.cadence.presentation.theme.CadenceTheme
import java.util.Date

@Composable
fun ListRefuels(
    refuels: List<RefuelEntity>,
    onDeleteRefuel: (RefuelEntity) -> Unit
) {
    val sortedRefuels = remember(refuels) { refuels.sortedByDescending { it.refuelNumber } }

    if (sortedRefuels.isEmpty()) {
        EmptyTabContent(Icons.Default.LocalGasStation, "Нет заправок")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedRefuels, key = { "refuel_${it.id}" }) { refuel ->
                RefuelCard(
                    refuel = refuel,
                    onDelete = { onDeleteRefuel(refuel) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RefuelsTabPreview() {
    val refuels = listOf(
        RefuelEntity(
            id = 1,
            cadenceId = 1,
            refuelNumber = 1,
            date = Date(),
            location = "LT",
            truckFuel = 500,
            adBlue = 40,
            trailerFuel = 100,
            trailerNumber = "RR 123",
            cardName = "DKV"
        )
    )
    CadenceTheme {
        ListRefuels(refuels = refuels, onDeleteRefuel = {})
    }
}
