package com.bcntransit.app.screens.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcntransit.app.BCNTransitApp.Screens.search.lines.BusLineItem
import com.bcntransit.app.BCNTransitApp.components.InlineErrorBanner
import com.bcntransit.app.R
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.api.ApiService
import com.bcntransit.app.data.enums.TransportType
import com.bcntransit.app.model.transport.LineDto
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar

@Composable
fun BusLinesScreen(
    onLineClick: (LineDto) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: BusLinesViewModel = viewModel(
        factory = BusLinesViewModelFactory(ApiClient.busApiService)
    )
    val lines by viewModel.lines.collectAsState()
    val loadingLines by viewModel.loadingLines.collectAsState()
    val errorLines by viewModel.errorLines.collectAsState()
    val expandedStates by viewModel.expandedStates.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CustomTopBar(
                onBackClick = onBackClick,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val drawableId = remember(TransportType.BUS) {
                            context.resources.getIdentifier(
                                TransportType.BUS.type, "drawable", context.packageName
                            )
                        }
                        Icon(
                            painter = painterResource(drawableId),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            stringResource(R.string.bus_lines),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                loadingLines -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(R.color.medium_red))
                    }
                }

                errorLines != null -> {
                    // Añadimos padding al error también para que se alinee
                    Box(modifier = Modifier.padding(16.dp)) {
                        InlineErrorBanner(errorLines!!)
                    }
                }

                else -> {
                    val groupedByCategory = remember(lines) {
                        lines.groupBy { viewModel.mapToCustomCategory(it) }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        groupedByCategory.forEach { (category, linesInCategory) ->
                            item {
                                BusCategoryCard(
                                    category = category,
                                    lines = linesInCategory,
                                    isExpanded = expandedStates[category] == true,
                                    onToggle = { viewModel.toggleCategory(category) },
                                    onLineClick = onLineClick,
                                    viewModel = viewModel,
                                    context = context
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusCategoryCard(
    category: String,
    lines: List<LineDto>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onLineClick: (LineDto) -> Unit,
    viewModel: BusLinesViewModel,
    context: android.content.Context
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Column {
            CategoryHeaderStyled(
                title = category,
                count = lines.size,
                isExpanded = isExpanded,
                onToggle = onToggle
            )

            if (isExpanded) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )

                Column {
                    lines.forEachIndexed { index, line ->
                        BusLineItem(
                            line = line,
                            onClick = { onLineClick(line) },
                            drawableId = viewModel.mapLineToDrawableId(line, context)
                        )
                        if (index < lines.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryHeaderStyled(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count líneas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.rotate(rotationState)
            )
        }
    }
}

class BusLinesViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusLinesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusLinesViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}