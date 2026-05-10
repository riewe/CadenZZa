package by.riewe.cadence.presentation.screens.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.presentation.common.components.RouteCard
import by.riewe.cadence.presentation.screens.cadence.EmptyTabContent
import by.riewe.cadence.presentation.theme.CadenceTheme
import java.util.Date

@Composable
fun ListRoutes(
    routes: List<RouteEntity>,
    onRouteClick: (Long) -> Unit,
    onDeleteRoute: (Long) -> Unit
) {
    val sortedRoutes = remember(routes) { routes.sortedByDescending { it.routeNumber } }

    if (sortedRoutes.isEmpty()) {
        EmptyTabContent(Icons.Default.AddRoad, "Нет рейсов")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedRoutes, key = { "route_${it.id}" }) { route ->
                RouteCard(
                    route = route,
                    onClick = { onRouteClick(route.id) },
                    onDelete = { onDeleteRoute(route.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoutesTabPreview() {
    val routes = listOf(
        RouteEntity(
            id = 1,
            cadenceId = 1,
            routeNumber = 2,
            startLocation = "LT",
            endLocation = "DE",
            startDate = Date(),
            endDate = Date(),
            startOdometer = 100000,
            endOdometer = 101000,
            startEngineHours = 1000,
            endEngineHours = 1020,
            goodsDescription = "Apple",
            isActive = false
        ),
        RouteEntity(
            id = 2,
            cadenceId = 1,
            routeNumber = 3,
            startLocation = "DE",
            startDate = Date(),
            startOdometer = 101000,
            startEngineHours = 1020,
            goodsDescription = "Banana",
            isActive = true
        )
    )
    CadenceTheme {
        ListRoutes(routes = routes, onRouteClick = {}, onDeleteRoute = {})
    }
}
