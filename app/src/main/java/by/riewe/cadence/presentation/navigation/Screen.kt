package by.riewe.cadence.presentation.navigation

sealed class Screen(val route: String) {
    object List : Screen("cadence_list")
    object Create : Screen("cadence_create?suggestedNumber={suggestedNumber}") {
        fun createRoute(suggestedNumber: String?) = "cadence_create?suggestedNumber=$suggestedNumber"
    }
    object Detail : Screen("cadence_detail/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "cadence_detail/$cadenceId"
    }
    object Close : Screen("cadence_close/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "cadence_close/$cadenceId"
    }
    object Edit : Screen("cadence_edit/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "cadence_edit/$cadenceId"
    }

    // Routes (Рейсы)
    object RouteCreate : Screen("route_create/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "route_create/$cadenceId"
    }
    object RouteClose : Screen("route_close/{routeId}") {
        fun createRoute(routeId: Long) = "route_close/$routeId"
    }

    // Refuelings (Заправки)
    object RefuelList : Screen("refuel_list/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "refuel_list/$cadenceId"
    }
    object RefuelCreate : Screen("refuel_create/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "refuel_create/$cadenceId"
    }

    // Expenses (Расходы)
    object ExpenseCreate : Screen("expense_create/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "expense_create/$cadenceId"
    }

    // Trailer Changes (Перецепы)
    object TrailerChangeCreate : Screen("trailer_change_create/{cadenceId}") {
        fun createRoute(cadenceId: Long) = "trailer_change_create/$cadenceId"
    }
    object TrailerChangeEdit : Screen("trailer_change_edit/{changeId}") {
        fun createRoute(changeId: Long) = "trailer_change_edit/$changeId"
    }

    // Settings (Настройки)
    object TruckSettings : Screen("truck_settings/{truckNumber}") {
        fun createRoute(truckNumber: String) = "truck_settings/$truckNumber"
    }
}
