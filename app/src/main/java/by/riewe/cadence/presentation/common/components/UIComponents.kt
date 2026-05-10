package by.riewe.cadence.presentation.common.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.riewe.cadence.R
import by.riewe.cadence.data.local.entities.ExpenseEntity
import by.riewe.cadence.data.local.entities.RefuelEntity
import by.riewe.cadence.data.local.entities.RouteEntity
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.domain.model.CadenceStatistics
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.*
import by.riewe.cadence.presentation.theme.StatusActive
import by.riewe.cadence.presentation.theme.StatusClosed
import by.riewe.cadence.utils.CountryFlags
import by.riewe.cadence.utils.Currencies
import by.riewe.cadence.utils.FuelCards
import by.riewe.cadence.utils.formatDate
import by.riewe.cadence.utils.formatNumberWithSpaces
import by.riewe.cadence.utils.formatPlate
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Основная Карточка
 */
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            hoveredElevation = 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        content = content
    )
}


/**
 * Универсальная карточка для отображения каденции.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CadenceCard(
    modifier: Modifier = Modifier,
    cadenceNum: Int? = null,
    truckPlate: String,
    currentTrailer: String,
    cadenceStart: String? = null,
    cadenceEnd: String? = null,
    isActive: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    accentColor: Color = if (isActive) StatusActive else StatusClosed,
    isExpanded: Boolean = false,
    largeContent: @Composable ColumnScope.() -> Unit = {},
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    val finish = cadenceEnd ?: "..."
    val isDark = isSystemInDarkTheme()

    val statusTextColor = if (isActive) {
        if (isDark) Color(0xFFB9F6CA) else Color(0xFF003300)
    } else {
        if (isDark) Color(0xFFEEEEEE) else Color(0xFF111111)
    }

    MainCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Каденция № $cadenceNum",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = accentColor.copy(alpha = if (isDark) 1f else 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isActive) "Активна" else "Закрыта",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = statusTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.5.dp,
                    color = OrangePrimary.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.truck),
                            contentDescription = "Тягач",
                            tint = OrangePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        LithuanianPlate(number = truckPlate)
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.trailer),
                            contentDescription = "Прицеп",
                            tint = OrangePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        LithuanianPlate(number = currentTrailer)
                    }
                }

                if (cadenceStart != null) {
                    Text(
                        text = "с $cadenceStart по $finish",
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp),
                    thickness = 0.5.dp,
                    color = OrangePrimary.copy(alpha = 0.5f)
                )
                
                if (expanded) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        largeContent()
                    }
                }
            }
        }
        
        val actionLabel = if (expanded) "Свернуть каденцию № $cadenceNum" 
                         else "Развернуть каденцию № $cadenceNum"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = actionLabel) { expanded = !expanded }
                .background(color = OrangePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = actionLabel,
                modifier = Modifier.padding(vertical = 8.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * Карточка для отображения информации о рейсе.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteCard(
    route: RouteEntity,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    var showMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val statusTextColor = if (route.isActive) {
        if (isDark) Color(0xFFB9F6CA) else Color(0xFF003300)
    } else {
        if (isDark) Color(0xFFEEEEEE) else Color(0xFF111111)
    }
    MainCard(
        modifier = modifier
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { if (onDelete != null) showMenu = true }
                    )
                    .padding(16.dp)
            ) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Удалить эту запись") },
                        onClick = {
                            showMenu = false
                            onDelete?.invoke()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Рейс №${route.routeNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    
                    if (!route.isActive) {

                        val accentColor = StatusClosed
                        val statusTextColor = if (isDark) Color(0xFFEEEEEE) else Color(0xFF111111)

                        Surface(
                            color = accentColor.copy(alpha = if (isDark) 1f else 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                text = "ВЫПОЛНЕН",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusTextColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.5.dp,
                    color = OrangePrimary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CountryFlagAndName(
                        countryCode = route.startLocation,
                        modifier = Modifier.weight(1.1f),
                        horizontalAlignment = Alignment.Start
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        tint = OrangePrimary
                    )

                    if (route.endLocation != null) {
                        CountryFlagAndName(
                            countryCode = route.endLocation,
                            modifier = Modifier.weight(1.1f),
                            horizontalAlignment = Alignment.End
                        )
                    } else {
                        Surface(
                            color = StatusActive.copy(alpha = if (isDark) 1f else 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "В пути",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = statusTextColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(route.startDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = route.goodsDescription,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = route.endDate?.let { formatDate(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = OrangePrimary.copy(alpha = 0.5f)
                        )

                        route.cmrNumber?.let {
                            if (it != "SKIPPED") InfoRowItem("CMR №:", it)
                        }

                        if (!route.trailerNumber.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Прицеп:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LithuanianPlate(number = route.trailerNumber)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Одометр (отпр.):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${formatNumberWithSpaces(route.startOdometer.toString())} км",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic)
                            }
                            route.endOdometer?.let {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Одометр (приб.):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${formatNumberWithSpaces(it.toString())} км",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Моточасы (нач.):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${formatNumberWithSpaces(route.startEngineHours.toString())} мч",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic)
                            }
                            route.endEngineHours?.let {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Моточасы (кон.):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${formatNumberWithSpaces(it.toString())} мч",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic)
                                }
                            }
                        }

                        InfoRowItem("Режим реф.:", route.agregateMode)

                        if (!route.temperatureValue.isNullOrEmpty()) {
                            InfoRowItem("Температура:", route.temperatureValue)
                        }
                        route.weight?.let {
                            InfoRowItem("Вес:",
                                "${String.format("%.3f", it).replace('.', ',')} т",
                                isBold = true,
                                isItalic = true)
                        }

                        if (route.endLocation != null) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = OrangePrimary.copy(alpha = 0.5f)
                            )
                            
                            route.mileAge?.let {
                                InfoRowItem("Общий пробег:", "${formatNumberWithSpaces(it.toString())} км", isBold = true, isItalic = true)
                            }
                            
                            route.fuelBurned?.let {
                                InfoRowItem("Потрачено топлива:", "${formatNumberWithSpaces(it.toString())} л", isBold = true, isItalic = true)
                            }
                            
                            route.fuelConsumption?.let {
                                InfoRowItem("Средний расход:", "${String.format("%.1f", it).replace('.', ',')} л/100км", isBold = true, isItalic = true)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(color = OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    modifier = Modifier.padding(vertical = 4.dp),
                    tint = Color.White
                )
            }
        }
    }
}


/**
 * Карточка заправки
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefuelCard(
    refuel: RefuelEntity,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    MainCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { /* Просто для активации длинного нажатия */ },
                    onLongClick = { if (onDelete != null) showMenu = true }
                )
                .padding(16.dp)
        ) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Удалить эту запись") },
                    onClick = {
                        showMenu = false
                        onDelete?.invoke()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Заправка №${refuel.refuelNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatDate(refuel.date),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.5.dp,
                color = OrangePrimary.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (refuel.truckFuel > 0) {
                    FuelInfoItem(
                        label = "Тягач",
                        amount = refuel.truckFuel,
                        icon = painterResource(id = R.drawable.truck)
                    )
                }
                if (refuel.adBlue > 0) {
                    FuelInfoItem(
                        label = "AdBlue",
                        amount = refuel.adBlue,
                        icon = painterResource(id = R.drawable.adblue),
                        iconTint = AdBlueColor,
                        iconSize = 28.dp
                    )
                }

            }
            if (refuel.trailerFuel > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = OrangePrimary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (refuel.trailerFuel > 0) {
                    FuelInfoItem(
                        label = "Прицеп",
                        amount = refuel.trailerFuel,
                        icon = painterResource(id = R.drawable.trailer)
                    )
                }
                if (!refuel.trailerNumber.isNullOrEmpty()) {

                    Spacer(modifier = Modifier.width(8.dp))
                    LithuanianPlate(number = refuel.trailerNumber)

                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = OrangePrimary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = CountryFlags.getFlag(refuel.location),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = refuel.location,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = refuel.cardName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FuelInfoItem(
    label: String,
    amount: Int,
    icon: Painter? = null,
    iconTint: Color = OrangePrimary,
    iconSize: Dp = 32.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "${formatNumberWithSpaces(amount.toString())} л",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

/**
 * Карточка расходов
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    MainCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { /* Просто для активации длинного нажатия */ },
                    onLongClick = { if (onDelete != null) showMenu = true }
                )
                .padding(16.dp)
        ) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Удалить эту запись") },
                    onClick = {
                        showMenu = false
                        onDelete?.invoke()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Расход №${expense.expenseNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatDate(Date(expense.date)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.5.dp,
                color = OrangePrimary.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.description ?: "Без описания",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.2f", expense.amount).replace('.', ','),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = expense.currency,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = CountryFlags.getFlag(expense.location),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = expense.location,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = expense.cardName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


/**
 * Карточка перецепа
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrailerChangeCard(
    trailerChange: TrailerChangeEntity,
    onClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    MainCard(
        modifier = modifier.then(
            Modifier.combinedClickable(
                onClick = { onClick?.invoke() },
                onLongClick = { showMenu = true }
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (onEdit != null) {
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = OrangePrimary
                            )
                        }
                    )
                }
                if (onDelete != null) {
                    DropdownMenuItem(
                        text = { Text("Удалить эту запись") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (trailerChange.changeNumber == 0) "Начальный прицеп" else "Перецеп №${trailerChange.changeNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatDate(Date(trailerChange.startDate)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.5.dp,
                color = OrangePrimary.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Принято от тягача:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LithuanianPlate(number = trailerChange.donorTruckNumber)
                }
                Column {
                    Text(
                        text = "Прицеп:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LithuanianPlate(number = trailerChange.trailerNumber)
                }


            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Топливо (прицеп):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${formatNumberWithSpaces(trailerChange.startTrailerFuel.toString())} л",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = OrangePrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Моточасы (нач.):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${formatNumberWithSpaces(trailerChange.startEngineHours.toString())} мч",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        color = OrangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = OrangePrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = CountryFlags.getFlag(trailerChange.startLocation),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trailerChange.startLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (!trailerChange.isActive && trailerChange.endLocation != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp).size(16.dp),
                        tint = OrangePrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = CountryFlags.getFlag(trailerChange.endLocation),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trailerChange.endLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!trailerChange.isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = OrangePrimary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Топливо (конец):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${trailerChange.endTrailerFuel?.let { formatNumberWithSpaces(it.toString()) } ?: "-"} л",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Моточасы (кон.):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${trailerChange.endEngineHours?.let { formatNumberWithSpaces(it.toString()) } ?: "-"} мч",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic)
                    }
                }
                
                if (trailerChange.totalEngineHours != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRowItem("Отработано мч:", "${formatNumberWithSpaces(trailerChange.totalEngineHours.toString())} мч", isBold = true, isItalic = true)
                }
            }
        }
    }
}


/**
 * Карточка статистики топлива (раскрывающаяся)
 */
@Composable
fun StatisticsCard(
    stats: CadenceStatistics,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(isExpanded) }

    MainCard(modifier = modifier) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Статистика",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.5.dp,
                    color = OrangePrimary.copy(alpha = 0.5f)
                )

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem("Пробег", "${formatNumberWithSpaces(stats.totalMileage.toString())} км", Modifier.weight(1f))
                            StatItem("Общий вес", "${String.format("%.1f", stats.totalWeight).replace('.', ',')} т", Modifier.weight(1f))
                            StatItem("Ср. норма", "${String.format("%.1f", stats.averageConsumptionNormal).replace('.', ',')} л", Modifier.weight(1f))
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = OrangePrimary.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem("Расходы", "${String.format("%.2f", stats.totalExpenses).replace('.', ',')} EUR", Modifier.weight(1f))
                            StatItem("Моточасы (пр)", "${formatNumberWithSpaces(stats.totalTrailerEngineHours.toString())} мч", Modifier.weight(1f))
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = OrangePrimary.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Расход (норма)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.1f", stats.totalFuelNormal).replace('.', ',')} л",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Заправлено",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${formatNumberWithSpaces(stats.totalRefueled.toString())} л",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Остаток (расч.)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.1f", stats.currentFuelLevel).replace('.', ',')} л",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = if (stats.currentFuelLevel > 300) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(color = OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    modifier = Modifier.padding(vertical = 4.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun FuelCardPicker(
    selectedCard: String,
    onCardSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Способ оплаты"
) {
    var expanded by remember { mutableStateOf(false) }
    val cards = FuelCards.list

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCard,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = OrangePrimary) },
            placeholder = { Text("Карта") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            cards.forEach { card ->
                DropdownMenuItem(
                    text = {
                        Text(text = card)
                    },
                    onClick = {
                        onCardSelected(card)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencyPicker(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Валюта"
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = Currencies.list

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.Payments, null, tint = OrangePrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(text = currency)
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CountrySelector(
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Страна"
) {
    var expanded by remember { mutableStateOf(false) }
    val countries = CountryFlags.getAllCountryCodes()

    Box(modifier = modifier) {
        OutlinedTextField(
            value = "${CountryFlags.getFlag(selectedCountryCode)} $selectedCountryCode ${CountryFlags.getName(selectedCountryCode)}",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.LocationOn,
                null, tint = OrangePrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            countries.forEach { code ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = CountryFlags.getFlag(code), fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "$code ${CountryFlags.getName(code)}")
                        }
                    },
                    onClick = {
                        onCountrySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun LithuanianPlate(
    number: String,
    modifier: Modifier = Modifier
) {
    val formattedNumber = formatPlate(number)

    Row(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
            .background(Color.White, RoundedCornerShape(4.dp))
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(26.dp)
                .background(
                    color = Color(0xFF003399),
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                repeat(12) { i ->
                    val angle = i * (2 * PI / 12)
                    val radius = 5.2.dp
                    val x = (radius.value * cos(angle)).dp
                    val y = (radius.value * sin(angle)).dp
                    
                    Text(
                        text = "★",
                        color = Color(0xFFFFD700),
                        fontSize = 3.2.sp,
                        lineHeight = 3.2.sp,
                        modifier = Modifier.offset(x = x, y = y)
                    )
                }
            }
            
            Text(
                text = "LT",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 9.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Text(
            text = formattedNumber.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun CountryFlagAndName(
    countryCode: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (horizontalAlignment == Alignment.End) {
                Text(
                    text = CountryFlags.getName(countryCode),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = CountryFlags.getFlag(countryCode), fontSize = 20.sp)
            } else {
                Text(text = CountryFlags.getFlag(countryCode), fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = CountryFlags.getName(countryCode),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun InfoRowItem(label: String, value: String, isBold: Boolean = false, isItalic: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Универсальный диалог подтверждения удаления
 */
@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Подтверждение удаления",
    text: String = "Вы уверены, что хотите удалить эту запись? Это действие нельзя отменить."
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Previews

@Preview(showBackground = true, name = "Active State Centered")
@Composable
fun CadenceCardActivePreview() {
    CadenceTheme {
        CadenceCard(
            cadenceNum = 2,
            truckPlate = "MOD 455",
            currentTrailer = "ZR 377",
            cadenceStart = "01.10.2023",
            isActive = true
        )
    }
}

@Preview(showBackground = true, name = "Route Cards Preview")
@Composable
fun RouteCardsPreview() {
    val activeRoute = RouteEntity(
        id = 1,
        cadenceId = 1,
        routeNumber = 3,
        startLocation = "LT",
        startDate = Date(),
        startOdometer = 125000,
        startEngineHours = 4500,
        goodsDescription = "Электроника",
        weight = 12.123,
        agregateMode = "Автомат",
        temperatureValue = "-18",
        isActive = true
    )
    
    CadenceTheme {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RouteCard(route = activeRoute, onClick = {}, onDelete = {})
        }
    }
}

@Preview(showBackground = true, name = "Expense Card Preview")
@Composable
fun ExpenseCardPreview() {
    val expense = ExpenseEntity(
        id = 1,
        cadenceId = 1,
        expenseNumber = 5,
        date = System.currentTimeMillis(),
        location = "DE",
        cardName = "TRAVIS",
        amount = 125.50,
        currency = "EUR",
        description = "Мойка прицепа"
    )

    CadenceTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            ExpenseCard(expense = expense, onDelete = {})
        }
    }
}

@Preview(showBackground = true, name = "Refuel Card Preview")
@Composable
fun RefuelCardPreview() {
    val refuel = RefuelEntity(
        id = 1,
        cadenceId = 1,
        refuelNumber = 3,
        date = Date(),
        location = "LT",
        truckFuel = 450,
        adBlue = 40,
        trailerFuel = 100,
        trailerNumber = "ZR 377",
        cardName = "DKV"
    )

    CadenceTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            RefuelCard(refuel = refuel, onDelete = {})
            Spacer(modifier = Modifier.height(8.dp))
            RefuelCard(
                refuel = refuel.copy(
                    id = 2,
                    refuelNumber = 2,
                    truckFuel = 600,
                    adBlue = 0,
                    trailerFuel = 0,
                    location = "PL",
                    cardName = "Shell",
                    trailerNumber = null
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Statistics Card Preview")
@Composable
fun StatisticsCardPreview() {
    val sampleStats = CadenceStatistics(
        totalMileage = 1954,
        totalFuelNormal = 450.0,
        averageConsumptionNormal = 23.5,
        totalRefueled = 500,
        currentFuelLevel = 150.5,
        totalWeight = 20.0,
        averageWeight = 10.2
    )

    CadenceTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            StatisticsCard(stats = sampleStats, isExpanded = true)
        }
    }
}

@Preview(showBackground = true, name = "Lithuanian Plate Preview")
@Composable
fun LithuanianPlatePreview() {
    CadenceTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LithuanianPlate(number = "NGK087")
            LithuanianPlate(number = "ZY569")
        }
    }
}

@Preview(showBackground = true, name = "Country Selector Preview")
@Composable
fun CountrySelectorPreview() {
    var selectedCountry by remember { mutableStateOf("LT") }
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CountrySelector(
                selectedCountryCode = selectedCountry,
                onCountrySelected = { selectedCountry = it }
            )
        }
    }
}

@Preview(showBackground = true, name = "Currency Picker Preview")
@Composable
fun CurrencyPickerPreview() {
    var selectedCurrency by remember { mutableStateOf("EUR") }
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CurrencyPicker(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it }
            )
        }
    }
}

@Preview(showBackground = true, name = "Fuel Card Picker Preview")
@Composable
fun FuelCardPickerPreview() {
    var selectedCard by remember { mutableStateOf("Circle K") }
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FuelCardPicker(
                selectedCard = selectedCard,
                onCardSelected = { selectedCard = it }
            )
        }
    }
}

@Preview(showBackground = true, name = "Trailer Change Card Preview")
@Composable
fun TrailerChangeCardPreview() {
    val trailerChange = TrailerChangeEntity(
        id = 1,
        cadenceId = 1,
        changeNumber = 2,
        startDate = System.currentTimeMillis() - 86400000,
        startLocation = "LT",
        startTrailerFuel = 150,
        startEngineHours = 1200,
        donorTruckNumber = "ZY 569",
        trailerNumber = "ZR 377",
        isActive = false,
        endLocation = "PL",
        endTrailerFuel = 130,
        endEngineHours = 1210,
        totalEngineHours = 10
    )

    CadenceTheme {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TrailerChangeCard(trailerChange = trailerChange)
            TrailerChangeCard(
                trailerChange = trailerChange.copy(
                    id = 2,
                    changeNumber = 3,
                    isActive = true,
                    endLocation = null,
                    endTrailerFuel = null,
                    endEngineHours = null,
                    totalEngineHours = null,
                    startLocation = "PL"
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Confirm Delete Dialog Preview")
@Composable
fun ConfirmDeleteDialogPreview() {
    CadenceTheme {
        ConfirmDeleteDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Common Components Preview")
@Composable
fun CommonComponentsPreview() {
    CadenceTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem(label = "Label", value = "Value")
            
            HorizontalDivider(color = OrangePrimary.copy(alpha = 0.5f))
            
            CountryFlagAndName(countryCode = "DE")
            
            CountryFlagAndName(
                countryCode = "PL",
                horizontalAlignment = Alignment.End
            )
            
            HorizontalDivider(color = OrangePrimary.copy(alpha = 0.5f))
            
            InfoRowItem(label = "Info Label", value = "Info Value")
            InfoRowItem(label = "Bold Label", value = "Bold Value", isBold = true)
        }
    }
}

/**
 * Выбор описания расхода
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDescriptionPicker(
    selectedDescription: String,
    onDescriptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Описание расхода"
) {
    var expanded by remember { mutableStateOf(false) }
    val descriptions = by.riewe.cadence.utils.ExpenseDescription.list

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDescription,
            onValueChange = { onDescriptionSelected(it) },
            label = { Text(label) },
            readOnly = false,
            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, null, tint = OrangePrimary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            descriptions.forEach { description ->
                DropdownMenuItem(
                    text = { Text(description) },
                    onClick = {
                        onDescriptionSelected(description)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RefrigeModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val modes = listOf("Выключен", "Автомат", "Постоянка")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedMode,
            onValueChange = {},
            label = { Text("Режим работы рефрижератора") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.Thermostat, contentDescription = null, tint = OrangePrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            modes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(text = mode) },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RouteInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
            } else if (painterIcon != null) {
                Icon(painterIcon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(24.dp))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            focusedLabelColor = OrangePrimary,
            cursorColor = OrangePrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
}

@Composable
fun RouteInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = OrangePrimary)
            } else if (painterIcon != null) {
                Icon(painterIcon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(24.dp))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangePrimary,
            focusedLabelColor = OrangePrimary,
            cursorColor = OrangePrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RefrigeModeSelectorPreview() {
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RefrigeModeSelector(selectedMode = "Автомат", onModeSelected = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteInputFieldPreview() {
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RouteInputField(
                value = "123456",
                onValueChange = {},
                label = "Одометр",
                icon = Icons.Default.Speed
            )
        }
    }
}
