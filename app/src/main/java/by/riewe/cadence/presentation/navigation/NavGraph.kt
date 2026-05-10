package by.riewe.cadence.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import by.riewe.cadence.presentation.screens.cadence.CadenceDetailScreen
import by.riewe.cadence.presentation.screens.cadence.CadenceListScreen
import by.riewe.cadence.presentation.screens.cadence.CloseCadenceScreen
import by.riewe.cadence.presentation.screens.cadence.CreateCadenceScreen
import by.riewe.cadence.presentation.screens.expenses.AddExpenseScreen
import by.riewe.cadence.presentation.screens.refuelings.AddRefuelScreen
import by.riewe.cadence.presentation.screens.refuelings.RefuelListScreen
import by.riewe.cadence.presentation.screens.routes.AddRouteDataScreen
import by.riewe.cadence.presentation.screens.settings.TruckSettingsScreen
import by.riewe.cadence.presentation.screens.trailer_change.AddTrailerChangeScreen
import by.riewe.cadence.presentation.viewmodel.CadenceViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    cadenceViewModel: CadenceViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        // 1. Список каденций
        composable(Screen.List.route) {
            CadenceListScreen(
                viewModel = cadenceViewModel,
                onNavigateToCreate = { suggested ->
                    navController.navigate(Screen.Create.createRoute(suggested))
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateToClose = { id ->
                    navController.navigate(Screen.Close.createRoute(id))
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.Edit.createRoute(id))
                }
            )
        }

        // 2. Создание каденции
        composable(
            route = Screen.Create.route,
            arguments = listOf(
                navArgument("suggestedNumber") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            CreateCadenceScreen(
                viewModel = cadenceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 3. Детали каденции
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            CadenceDetailScreen(
                cadenceId = id,
                viewModel = cadenceViewModel,
                onBack = { navController.popBackStack() },
                onAddRoute = {
                    navController.navigate(Screen.RouteCreate.createRoute(id))
                },
                onAddRefuel = {
                    navController.navigate(Screen.RefuelCreate.createRoute(id))
                },
                onAddExpense = {
                    navController.navigate(Screen.ExpenseCreate.createRoute(id))
                },
                onTrailerChange = {
                    navController.navigate(Screen.TrailerChangeCreate.createRoute(id))
                },
                onCloseCadence = {
                    navController.navigate(Screen.Close.createRoute(id))
                },
                onOpenSettings = { truckNumber ->
                    navController.navigate(Screen.TruckSettings.createRoute(truckNumber))
                },
                onRouteClick = { routeId ->
                    navController.navigate(Screen.RouteClose.createRoute(routeId))
                },
                onTrailerChangeClick = { /* Экран закрытия удален */ }
            )
        }

        // 5. Список заправок
        composable(
            route = Screen.RefuelList.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            RefuelListScreen(
                cadenceId = cadenceId,
                onBack = { navController.popBackStack() },
                onAddRefuel = {
                    navController.navigate(Screen.RefuelCreate.createRoute(cadenceId))
                }
            )
        }

        // 5a. Добавление заправки
        composable(
            route = Screen.RefuelCreate.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            AddRefuelScreen(
                cadenceId = cadenceId,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // 6. Настройки тягача
        composable(
            route = Screen.TruckSettings.route,
            arguments = listOf(
                navArgument("truckNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val truckNumber = backStackEntry.arguments?.getString("truckNumber") ?: ""
            TruckSettingsScreen(
                truckNumber = truckNumber,
                onBack = { navController.popBackStack() }
            )
        }

        // 4. Закрытие каденции
        composable(
            route = Screen.Close.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) {
            CloseCadenceScreen(
                viewModel = cadenceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Создание рейса
        composable(
            route = Screen.RouteCreate.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            AddRouteDataScreen(
                cadenceId = cadenceId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 8. Завершение рейса
        composable(
            route = Screen.RouteClose.route,
            arguments = listOf(
                navArgument("routeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getLong("routeId") ?: -1L
            AddRouteDataScreen(
                routeId = routeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 9. Добавление расхода
        composable(
            route = Screen.ExpenseCreate.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            AddExpenseScreen(
                cadenceId = cadenceId,
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // 10. Добавление перецепа
        composable(
            route = Screen.TrailerChangeCreate.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            AddTrailerChangeScreen(
                cadenceId = cadenceId,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // 11. Редактирование каденции
        composable(
            route = Screen.Edit.route,
            arguments = listOf(
                navArgument("cadenceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cadenceId = backStackEntry.arguments?.getLong("cadenceId") ?: -1L
            CreateCadenceScreen(
                viewModel = cadenceViewModel,
                cadenceId = cadenceId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
