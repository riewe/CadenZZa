package by.riewe.cadence.presentation.screens.cadence

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.riewe.cadence.R
import by.riewe.cadence.data.local.entities.CadenceEntity
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.model.CadenceStatistics
import by.riewe.cadence.presentation.common.components.*
import by.riewe.cadence.presentation.screens.trailer_change.ListTrailersChanges
import by.riewe.cadence.presentation.screens.expenses.ListExpenses
import by.riewe.cadence.presentation.screens.refuelings.ListRefuels
import by.riewe.cadence.presentation.screens.routes.ListRoutes
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.StatusActive
import by.riewe.cadence.presentation.theme.StatusClosed
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.presentation.viewmodel.*
import by.riewe.cadence.utils.calculateDaysInWork
import by.riewe.cadence.utils.formatDate
import java.util.*

enum class CadenceDetailTab(val label: String, val icon: Any) {
    ROUTES("Рейсы", Icons.Default.AddRoad),
    REFUELS("Заправки", Icons.Default.LocalGasStation),
    EXPENSES("Расходы", Icons.Default.ShoppingCart),
    CHANGES("Перецепы", R.drawable.trailer)
}

@Composable
fun CadenceDetailScreen(
    cadenceId: Long,
    viewModel: CadenceViewModel = hiltViewModel(),
    routeViewModel: RouteViewModel = hiltViewModel(),
    refuelViewModel: RefuelViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    trailerChangeViewModel: TrailerChangeViewModel = hiltViewModel(),
    statsViewModel: StatisticsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onAddRoute: (Long) -> Unit,
    onAddRefuel: (Long) -> Unit,
    onAddExpense: (Long) -> Unit,
    onTrailerChange: (Long) -> Unit,
    onCloseCadence: (Long) -> Unit,
    onOpenSettings: (String) -> Unit,
    onRouteClick: (Long) -> Unit,
    onTrailerChangeClick: (Long) -> Unit
) {
    val cadence by remember(cadenceId) { viewModel.getCadence(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = null)
    val routes by remember(cadenceId) { routeViewModel.getRoutes(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val refuels by remember(cadenceId) { refuelViewModel.getRefuels(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val expenses by remember(cadenceId) { expenseViewModel.getExpenses(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val trailerChanges by remember(cadenceId) { trailerChangeViewModel.getChanges(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val stats by remember(cadenceId) { statsViewModel.getStatistics(cadenceId) }
        .collectAsStateWithLifecycle(initialValue = null)
    
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    if (itemToDelete != null) {
        ConfirmDeleteDialog(
            onConfirm = {
                when (val item = itemToDelete) {
                    is RouteEntity -> routeViewModel.deleteRoute(item.id)
                    is RefuelEntity -> refuelViewModel.deleteRefuel(item)
                    is ExpenseEntity -> expenseViewModel.deleteExpense(item)
                    is TrailerChangeEntity -> trailerChangeViewModel.deleteTrailerChange(item)
                }
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    CadenceDetailContent(
        cadence = cadence,
        routes = routes,
        refuels = refuels,
        expenses = expenses,
        trailerChanges = trailerChanges,
        stats = stats,
        isLoading = isLoading,
        error = error,
        onBack = onBack,
        onAddRoute = onAddRoute,
        onAddRefuel = onAddRefuel,
        onAddExpense = onAddExpense,
        onTrailerChange = onTrailerChange,
        onCloseCadence = onCloseCadence,
        onOpenSettings = onOpenSettings,
        onRouteClick = onRouteClick,
        onTrailerChangeClick = { /* Ничего не делаем, экран закрытия удален */ },
        onEditTrailerChange = { changeId ->
            // Здесь должна быть навигация на экран редактирования
            onTrailerChangeClick(changeId)
        },
        onDeleteRoute = { routeId -> 
            routes.find { it.id == routeId }?.let { itemToDelete = it }
        },
        onDeleteRefuel = { itemToDelete = it },
        onDeleteExpense = { itemToDelete = it },
        onDeleteTrailerChange = { itemToDelete = it },
        onClearError = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadenceDetailContent(
    cadence: CadenceEntity?,
    routes: List<RouteEntity>,
    refuels: List<RefuelEntity>,
    expenses: List<ExpenseEntity>,
    trailerChanges: List<TrailerChangeEntity>,
    stats: CadenceStatistics?,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onAddRoute: (Long) -> Unit,
    onAddRefuel: (Long) -> Unit,
    onAddExpense: (Long) -> Unit,
    onTrailerChange: (Long) -> Unit,
    onCloseCadence: (Long) -> Unit,
    onOpenSettings: (String) -> Unit,
    onRouteClick: (Long) -> Unit,
    onTrailerChangeClick: (Long) -> Unit,
    onEditTrailerChange: (Long) -> Unit,
    onDeleteRoute: (Long) -> Unit,
    onDeleteRefuel: (RefuelEntity) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    onDeleteTrailerChange: (TrailerChangeEntity) -> Unit,
    onClearError: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(CadenceDetailTab.ROUTES) }
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) {
        CadenceDetailTab.entries.size
    }
    val scope = rememberCoroutineScope()

    // Синхронизация при свайпе
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = CadenceDetailTab.entries[pagerState.currentPage]
    }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }
    var showInfoSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            errorDialogMessage = error
            showErrorDialog = true
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(tonalElevation = 3.dp, shadowElevation = 4.dp) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 8.dp)) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Назад",
                                tint = OrangePrimary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Каденция № ${cadence?.cadenceNumber ?: "..."}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                cadence?.let { StatusBadge(isActive = it.isActive) }
                            }
                            cadence?.let { c ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "с ${formatDate(c.startDate)} по ${c.endDate?.let { formatDate(it) } ?: "..."}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    val days = calculateDaysInWork(c.startDate, c.endDate)
                                    Text(
                                        text = "$days дн.",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { showInfoSheet = true }) {
                            Icon(
                                Icons.Default.Info, 
                                contentDescription = "Информация", 
                                tint = OrangePrimary
                            )
                        }
                    }
                    cadence?.let { c ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).clickable { onOpenSettings(c.truckNumber) }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.truck),
                                    contentDescription = "Тягач",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                LithuanianPlate(number = c.truckNumber)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.trailer),
                                    contentDescription = "Прицеп",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val currentTrailer = trailerChanges.find { it.isActive }?.trailerNumber ?: c.trailerNumber
                                LithuanianPlate(number = currentTrailer)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                CadenceDetailTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            scope.launch {
                                pagerState.animateScrollToPage(tab.ordinal)
                            }
                        },
                        icon = {
                            val icon = tab.icon
                            if (icon is ImageVector) {
                                Icon(
                                    icon, 
                                    contentDescription = tab.label,
                                    tint = if (selectedTab == tab) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else if (icon is Int) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (selectedTab == tab) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = OrangePrimary,
                            indicatorColor = OrangePrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (cadence?.isActive == true) {
                val hasActiveRoute = routes.any { it.isActive }
                
                FloatingActionButton(
                    onClick = {
                        when (selectedTab) {
                            CadenceDetailTab.ROUTES -> {
                                if (hasActiveRoute) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Сначала завершите текущий рейс")
                                    }
                                } else {
                                    onAddRoute(cadence.id)
                                }
                            }
                            CadenceDetailTab.REFUELS -> onAddRefuel(cadence.id)
                            CadenceDetailTab.EXPENSES -> onAddExpense(cadence.id)
                            CadenceDetailTab.CHANGES -> onTrailerChange(cadence.id)
                        }
                    },
                    containerColor = if (selectedTab == CadenceDetailTab.ROUTES && hasActiveRoute) 
                                        MaterialTheme.colorScheme.surfaceVariant 
                                     else OrangePrimary,
                    contentColor = if (selectedTab == CadenceDetailTab.ROUTES && hasActiveRoute) 
                                        MaterialTheme.colorScheme.onSurfaceVariant 
                                   else Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить",
                        tint = if (selectedTab == CadenceDetailTab.ROUTES && hasActiveRoute) 
                                MaterialTheme.colorScheme.onSurfaceVariant 
                               else Color.White
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            cadence?.let { currentCadence ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (CadenceDetailTab.entries[page]) {
                        CadenceDetailTab.ROUTES -> {
                            ListRoutes(
                                routes = routes,
                                onRouteClick = onRouteClick,
                                onDeleteRoute = onDeleteRoute
                            )
                        }
                        CadenceDetailTab.REFUELS -> {
                            ListRefuels(
                                refuels = refuels,
                                onDeleteRefuel = onDeleteRefuel
                            )
                        }
                        CadenceDetailTab.EXPENSES -> {
                            ListExpenses(
                                expenses = expenses,
                                onDeleteExpense = onDeleteExpense
                            )
                        }
                        CadenceDetailTab.CHANGES -> {
                            ListTrailersChanges(
                                trailerChanges = trailerChanges,
                                onTrailerChangeClick = onTrailerChangeClick,
                                onEditTrailerChange = onEditTrailerChange,
                                onDeleteTrailerChange = onDeleteTrailerChange
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OrangePrimary
                )
            } else if (cadence == null) {
                Text(text = "Каденция не найдена", modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info, 
                        contentDescription = null, 
                        tint = OrangePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Статистика каденции",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                stats?.let { s ->
                    StatisticsCard(stats = s)
                } ?: Text("Нет данных для статистики")

                if (cadence?.isActive == true) {
                    Button(
                        onClick = {
                            showInfoSheet = false
                            onCloseCadence(cadence.id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Закрыть каденцию")
                    }
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Внимание") },
            text = { Text(errorDialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = OrangePrimary)
                ) { Text("ОК") }
            }
        )
    }
}

@Composable
fun EmptyTabContent(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp), 
            tint = OrangePrimary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EmptyTabContent(painter: androidx.compose.ui.graphics.painter.Painter, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp), 
            tint = OrangePrimary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun StatusBadge(
    isActive: Boolean,
    label: String? = null,
    activeColor: Color = StatusActive,
    closedColor: Color = StatusClosed
) {
    val text = label ?: if (isActive) "АКТИВНА" else "ЗАКРЫТА"
    val color = if (isActive) activeColor else closedColor
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CadenceDetailContentPreview() {
    val now = Date()
    val nowMillis = now.time
    
    CadenceTheme {
        CadenceDetailContent(
            cadence = CadenceEntity(
                id = 1,
                cadenceNumber = 1,
                startDate = now,
                startTime = 0L,
                driver1 = "Иван Иванов",
                driver2 = null,
                truckNumber = "AA 1234-7",
                initialOdometer = 100000,
                initialTruckFuel = 500,
                trailerNumber = "TR 5678",
                initialTrailerFuel = 100,
                initialEngineHours = 1200
            ),
            routes = listOf(
                RouteEntity(
                    id = 1,
                    cadenceId = 1,
                    routeNumber = 1,
                    startLocation = "Минск",
                    startDate = Date(),
                    startOdometer = 100000,
                    startEngineHours = 1200,
                    goodsDescription = "Электроника"
                )
            ),
            refuels = listOf(
                RefuelEntity(
                    id = 1,
                    cadenceId = 1,
                    refuelNumber = 1,
                    date = now,
                    location = "Берлин",
                    truckFuel = 400,
                    adBlue = 50,
                    trailerFuel = 100,
                    trailerNumber = "TR 5678",
                    cardName = "DKV"
                )
            ),
            expenses = listOf(
                ExpenseEntity(
                    id = 1,
                    cadenceId = 1,
                    expenseNumber = 1,
                    date = nowMillis,
                    location = "Варшава",
                    cardName = "DKV",
                    amount = 50.0,
                    currency = "EUR",
                    description = "Парковка"
                )
            ),
            trailerChanges = listOf(
                TrailerChangeEntity(
                    id = 1,
                    cadenceId = 1,
                    changeNumber = 0,
                    startDate = nowMillis,
                    trailerNumber = "TR 9999",
                    donorTruckNumber = "BB 4321-7",
                    startTrailerFuel = 50,
                    startEngineHours = 3000,
                    startLocation = "Познань"
                )
            ),
            stats = CadenceStatistics(
                totalMileage = 2250,
                totalFuelNormal = 750.0,
                averageConsumptionNormal = 33.3,
                totalRefueled = 750,
                currentFuelLevel = 100.0,
                totalWeight = 40.0,
                averageWeight = 20.0,
                totalExpenses = 50.0,
                totalTrailerEngineHours = 10
            ),
            isLoading = false,
            error = null,
            onBack = {},
            onAddRoute = {},
            onAddRefuel = {},
            onAddExpense = {},
            onTrailerChange = {},
            onCloseCadence = {},
            onOpenSettings = {},
            onRouteClick = {},
            onTrailerChangeClick = {},
            onEditTrailerChange = {},
            onDeleteRoute = {},
            onDeleteRefuel = {},
            onDeleteExpense = {},
            onDeleteTrailerChange = {},
            onClearError = {}
        )
    }
}
