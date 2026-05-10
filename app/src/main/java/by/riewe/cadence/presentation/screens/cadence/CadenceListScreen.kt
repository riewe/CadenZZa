package by.riewe.cadence.presentation.screens.cadence

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.riewe.cadence.R
import by.riewe.cadence.data.local.entities.CadenceEntity
import by.riewe.cadence.presentation.common.components.CadenceCard
import by.riewe.cadence.presentation.common.components.LithuanianPlate
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.viewmodel.CadenceViewModel
import by.riewe.cadence.utils.formatDate
import by.riewe.cadence.utils.formatNumberWithSpaces
import by.riewe.cadence.utils.formatTime
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Экран списка всех каденций на основе CadenceCard.
 */
@Composable
fun CadenceListScreen(
    viewModel: CadenceViewModel,
    onNavigateToCreate: (String?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToClose: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
) {
    val cadenceList by viewModel.allCadences.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading = viewModel.isLoading
    val suggestedNumber = viewModel.suggestedNumber
    val activeCadence = viewModel.activeCadence

    LaunchedEffect(Unit) {
        viewModel.loadActiveCadence()
    }

    CadenceListContent(
        cadenceList = cadenceList,
        isLoading = isLoading,
        suggestedNumber = suggestedNumber,
        activeCadence = activeCadence,
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToClose = onNavigateToClose,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteCadence = { viewModel.deleteCadence(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CadenceListContent(
    cadenceList: List<CadenceEntity>,
    isLoading: Boolean,
    suggestedNumber: String?,
    activeCadence: CadenceEntity?,
    onNavigateToCreate: (String?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToClose: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onDeleteCadence: (Long) -> Unit,
    initialExpanded: Boolean = false
) {
    var showCloseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var cadenceIdToClose by remember { mutableStateOf<Long?>(null) }
    var cadenceIdToDelete by remember { mutableStateOf<Long?>(null) }
    var cadenceIdToEdit by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Каденции", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (activeCadence == null) {
                        onNavigateToCreate(suggestedNumber)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Невозможно создать новую каденцию, пока активна №${activeCadence.cadenceNumber}",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                containerColor = if (activeCadence == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (activeCadence == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(Icons.Default.Add, contentDescription = "Создать каденцию")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (cadenceList.isEmpty()) {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    activeCadence = activeCadence,
                    onCreateClick = { 
                        if (activeCadence == null) {
                            onNavigateToCreate(suggestedNumber)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Сначала закройте активную каденцию")
                            }
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(cadenceList, key = { it.id }) { cadence ->
                        CadenceItem(
                            cadence = cadence,
                            onDetail = { onNavigateToDetail(cadence.id) },
                            onClose = { 
                                cadenceIdToClose = cadence.id
                                showCloseDialog = true
                            },
                            onEdit = {
                                cadenceIdToEdit = cadence.id
                                showEditDialog = true
                            },
                            onDelete = { 
                                cadenceIdToDelete = cadence.id
                                showDeleteDialog = true 
                            },
                            isExpanded = initialExpanded
                        )
                    }
                }
            }

            if (showCloseDialog) {
                AlertDialog(
                    onDismissRequest = { showCloseDialog = false },
                    title = { Text(stringResource(R.string.close_cadence_confirm_title)) },
                    text = { 
                        Text(stringResource(R.string.close_cadence_confirm_message)) 
                    },
                    confirmButton = {
                        Button(onClick = {
                            showCloseDialog = false
                            cadenceIdToClose?.let { onNavigateToClose(it) }
                        }) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCloseDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Удаление каденции") },
                    text = { 
                        Text("Вы уверены, что хотите удалить эту каденцию и ВСЕ связанные с ней данные (рейсы, заправки, расходы)? Это действие нельзя отменить.") 
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                cadenceIdToDelete?.let { onDeleteCadence(it) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Удалить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }

            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Редактирование каденции") },
                    text = { 
                        Text("Вы собираетесь редактировать основные данные каденции. Если каденция активна - доступно редактирование только начальных данных. Перед редактированием убедитесь в корректности новых данных.") 
                    },
                    confirmButton = {
                        Button(onClick = {
                            showEditDialog = false
                            cadenceIdToEdit?.let { onNavigateToEdit(it) }
                        }) {
                            Text("Редактировать")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    activeCadence: CadenceEntity?,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Нет каденций", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onCreateClick,
            enabled = activeCadence == null
        ) {
            Text("Создать первую каденцию")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CadenceItem(
    cadence: CadenceEntity,
    onDetail: () -> Unit,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isExpanded: Boolean = false
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        CadenceCard(
            cadenceNum = cadence.cadenceNumber,
            truckPlate = cadence.truckNumber,
            currentTrailer = cadence.trailerNumber,
            cadenceStart = formatDate(cadence.startDate),
            cadenceEnd = if (cadence.isActive) null else formatDate(cadence.endDate),
            isActive = cadence.isActive,
            isExpanded = isExpanded,
            onClick = onDetail,
            onLongClick = { menuExpanded = true },
            largeContent = {
                CadenceDetailsContent(
                    cadence = cadence,
                    onActionClick = onDetail
                )
            }
        )

        CadenceContextMenu(
            expanded = menuExpanded,
            onDismiss = { menuExpanded = false },
            onAction = { action ->
                when (action) {
                    is CadenceAction.CloseCadence -> onClose()
                    is CadenceAction.EditCadence -> onEdit()
                    is CadenceAction.DeleteCadence -> onDelete()
                }
                menuExpanded = false
            },
            isClosed = !cadence.isActive
        )
    }
}

@Composable
private fun CadenceDetailsContent(
    cadence: CadenceEntity,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Водители
        InfoRow("Водитель 1:", cadence.driver1)
        cadence.driver2?.let { InfoRow("Водитель 2:", it) }
        

        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        // Начальные данные
        InfoRow("Начало:", "${formatDate(cadence.startDate)} ${formatTime(cadence.startTime)}")
        InfoRow("Прицеп (нач.):") {
            LithuanianPlate(number = cadence.trailerNumber)
        }
        InfoRow("Одометр (нач.):", "${formatNumberWithSpaces(cadence.initialOdometer.toString())} км")
        InfoRow("Топливо тягач (нач.):", "${cadence.initialTruckFuel} л")
        InfoRow("Топливо реф (нач.):", "${cadence.initialTrailerFuel} л")
        InfoRow("Моточасы реф (нач.):", "${cadence.initialEngineHours} мч")

        if (!cadence.isActive) {
            HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
            
            // Финальные данные
            InfoRow("Окончание:", "${formatDate(cadence.endDate)} ${formatTime(cadence.endTime)}")
            InfoRow("Прицеп (кон.):") {
                LithuanianPlate(number = cadence.trailerNumber) // Заглушка
            }
            InfoRow("Одометр (кон.):", "${formatNumberWithSpaces(cadence.finalOdometer?.toString() ?: "0")} км")
            InfoRow("Топливо тягач (кон.):", "${cadence.finalTruckFuel ?: 0} л")
            InfoRow("Топливо реф (кон.):", "${cadence.finalTrailerFuel ?: 0} л")
            InfoRow("Моточасы реф (кон.):", "${cadence.finalEngineHours ?: 0} мч")
            
            HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
            
            // Итоги
            InfoRow("Общий пробег:", "${formatNumberWithSpaces(cadence.totalMileage?.toString() ?: "0")} км")
            
            // Расход (тягач)
            val truckFuelUsed = cadence.initialTruckFuel - (cadence.finalTruckFuel ?: 0)
            if (truckFuelUsed > 0 && (cadence.totalMileage ?: 0) > 0) {
                val consumption = (truckFuelUsed.toDouble() / cadence.totalMileage!!) * 100
                InfoRow("Ср. расход (тягач):", "%.1f л/100км".format(consumption))
            }
            
            // Расход (реф)
            val refFuelUsed = cadence.initialTrailerFuel - (cadence.finalTrailerFuel ?: 0)
            if (refFuelUsed > 0) {
                InfoRow("Расход реф:", "$refFuelUsed л")
            }
        }

        val daysText = if (cadence.isActive) {
            val diff = System.currentTimeMillis() - cadence.startDate.time
            TimeUnit.MILLISECONDS.toDays(diff).toString()
        } else {
            cadence.totalDays?.toString() ?: "-"
        }
        InfoRow("Дней в каденции:", daysText)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onActionClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(if (cadence.isActive) "Продолжить" else "Просмотр")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(150.dp)
        )
        content()
    }
}

sealed class CadenceAction {
    object CloseCadence : CadenceAction()
    object EditCadence : CadenceAction()
    object DeleteCadence : CadenceAction()
}

@Composable
fun CadenceContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAction: (CadenceAction) -> Unit,
    isClosed: Boolean
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        if (!isClosed) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.close_cadenzza)) },
                onClick = { onAction(CadenceAction.CloseCadence) },
                leadingIcon = { Icon(Icons.Default.PowerSettingsNew, null) }
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.edit)) },
            onClick = { onAction(CadenceAction.EditCadence) },
            leadingIcon = { Icon(Icons.Default.Edit, null) }
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) },
            onClick = { onAction(CadenceAction.DeleteCadence) },
            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
        )
    }
}

@Preview(showBackground = true, name = "List Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "List Dark")
@Composable
fun CadenceListPreview() {
    val mockCadences = listOf(
        CadenceEntity(
            id = 1,
            cadenceNumber = 3,
            startDate = Date(),
            startTime = 28000000L,
            driver1 = "Ivan Ivanov",
            driver2 = "Peter Petrov",
            truckNumber = "MOD 455",
            trailerNumber = "ZR 377",
            initialOdometer = 120000,
            initialTruckFuel = 400,
            initialTrailerFuel = 100,
            initialEngineHours = 5000,
            isActive = true
        ),
        CadenceEntity(
            id = 2,
            cadenceNumber = 2,
            startDate = Date(System.currentTimeMillis() - 864000000L),
            startTime = 32400000L,
            endDate = Date(System.currentTimeMillis() - 86400000L),
            endTime = 61200000L,
            driver1 = "Ivan Ivanov",
            driver2 = null,
            truckNumber = "NGK 087",
            trailerNumber = "ZY 569",
            initialOdometer = 110000,
            initialTruckFuel = 1000,
            initialTrailerFuel = 100,
            initialEngineHours = 4500,
            finalOdometer = 115000,
            finalTruckFuel = 200,
            finalTrailerFuel = 50,
            finalEngineHours = 4550,
            totalDays = 9,
            totalMileage = 5000,
            isActive = false
        )
    )
    CadenceTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CadenceListContent(
                cadenceList = mockCadences,
                isLoading = false,
                suggestedNumber = "124",
                onNavigateToCreate = {},
                onNavigateToDetail = {},
                onNavigateToClose = {},
                onNavigateToEdit = {},
                onDeleteCadence = {},
                activeCadence = null
            )
        }
    }
}

@Preview(showBackground = true, name = "List Active")
@Composable
fun CadenceListActivePreview() {
    val mockCadences = listOf(
        CadenceEntity(
            id = 1,
            cadenceNumber = 3,
            startDate = Date(),
            startTime = 28000000L,
            driver1 = "Ivan Ivanov",
            driver2 = "Peter Petrov",
            truckNumber = "MOD 455",
            trailerNumber = "ZR 377",
            initialOdometer = 120000,
            initialTruckFuel = 400,
            initialTrailerFuel = 100,
            initialEngineHours = 5000,
            isActive = true
        )
    )
    CadenceTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CadenceListContent(
                cadenceList = mockCadences,
                isLoading = false,
                suggestedNumber = "124",
                onNavigateToCreate = {},
                onNavigateToDetail = {},
                onNavigateToClose = {},
                onNavigateToEdit = {},
                onDeleteCadence = {},
                activeCadence = mockCadences[0]
            )
        }
    }
}

@Preview(showBackground = true, name = "Expanded List Light",
    device = "spec:width=411dp,height=1350dp,dpi=420")
@Composable
fun CadenceExpandedListPreview() {
    val mockCadences = listOf(
        CadenceEntity(
            id = 1,
            cadenceNumber = 3,
            startDate = Date(),
            startTime = 28000000L,
            driver1 = "Ivan Ivanov",
            driver2 = "Peter Petrov",
            truckNumber = "MOD 455",
            trailerNumber = "ZR 377",
            initialOdometer = 120000,
            initialTruckFuel = 400,
            initialTrailerFuel = 100,
            initialEngineHours = 5000,
            isActive = true
        ),
        CadenceEntity(
            id = 2,
            cadenceNumber = 2,
            startDate = Date(System.currentTimeMillis() - 864000000L),
            startTime = 32400000L,
            endDate = Date(System.currentTimeMillis() - 86400000L),
            endTime = 61200000L,
            driver1 = "Ivan Ivanov",
            driver2 = null,
            truckNumber = "NGK 087",
            trailerNumber = "ZY 569",
            initialOdometer = 110000,
            initialTruckFuel = 1000,
            initialTrailerFuel = 100,
            initialEngineHours = 4500,
            finalOdometer = 115000,
            finalTruckFuel = 200,
            finalTrailerFuel = 50,
            finalEngineHours = 4550,
            totalDays = 9,
            totalMileage = 5000,
            isActive = false
        )
    )
    CadenceTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CadenceListContent(
                cadenceList = mockCadences,
                isLoading = false,
                suggestedNumber = "124",
                onNavigateToCreate = {},
                onNavigateToDetail = {},
                onNavigateToClose = {},
                onNavigateToEdit = {},
                onDeleteCadence = {},
                initialExpanded = true,
                activeCadence = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CadenceContextMenuPreview() {
    CadenceTheme {
        // Оборачиваем в Box, чтобы меню было к чему привязаться
        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            CadenceContextMenu(
                expanded = true, // Всегда открыто для превью
                onDismiss = {},
                onAction = {},
                isClosed = false
            )
        }
    }
}
