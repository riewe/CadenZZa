package by.riewe.cadence.presentation.screens.trailer_change

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import by.riewe.cadence.R
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.presentation.common.components.TrailerChangeCard
import by.riewe.cadence.presentation.screens.cadence.EmptyTabContent
import by.riewe.cadence.presentation.theme.CadenceTheme

@Composable
fun ListTrailersChanges(
    trailerChanges: List<TrailerChangeEntity>,
    onTrailerChangeClick: (Long) -> Unit,
    onEditTrailerChange: (Long) -> Unit,
    onDeleteTrailerChange: (TrailerChangeEntity) -> Unit
) {
    val sortedChanges = remember(trailerChanges) { trailerChanges.sortedByDescending { it.changeNumber } }

    if (sortedChanges.isEmpty()) {
        EmptyTabContent(painterResource(id = R.drawable.trailer), "Нет перецепов")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedChanges, key = { "change_${it.id}" }) { change ->
                TrailerChangeCard(
                    trailerChange = change,
                    onClick = null,
                    onEdit = { onEditTrailerChange(change.id) },
                    onDelete = { onDeleteTrailerChange(change) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListTrailersChangesPreview() {
    val changes = listOf(
        TrailerChangeEntity(
            id = 1,
            cadenceId = 1,
            changeNumber = 0,
            startDate = System.currentTimeMillis() - 86400000,
            trailerNumber = "ZR 123",
            donorTruckNumber = "START",
            startTrailerFuel = 200,
            startEngineHours = 5000,
            startLocation = "LT",
            isActive = false
        ),
        TrailerChangeEntity(
            id = 2,
            cadenceId = 1,
            changeNumber = 1,
            startDate = System.currentTimeMillis(),
            trailerNumber = "AA 456",
            donorTruckNumber = "BC 789",
            startTrailerFuel = 150,
            startEngineHours = 5100,
            startLocation = "PL",
            isActive = true
        )
    )
    CadenceTheme {
        ListTrailersChanges(
            trailerChanges = changes,
            onTrailerChangeClick = {},
            onEditTrailerChange = {},
            onDeleteTrailerChange = {}
        )
    }
}