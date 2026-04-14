package com.example.myapplication.ui.fleet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetStatus
import com.example.myapplication.data.model.Locomotive
import com.example.myapplication.data.model.RollingStock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetDashboard(
    viewModel: FleetViewModel,
    onLocomotiveClick: (String) -> Unit,
    onRollingStockClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fleet_inventory)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Asset Filter Tabs
            AssetFilterTabs(
                selectedFilter = uiState.assetFilter,
                onFilterSelected = viewModel::updateAssetFilter
            )

            // Status Filter Chips
            StatusFilterChips(
                selectedStatus = uiState.statusFilter,
                onStatusSelected = viewModel::updateStatusFilter,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.locomotives) { loco ->
                        AssetCard(
                            name = loco.name,
                            subtitle = loco.model,
                            status = loco.status,
                            icon = Icons.Default.Train,
                            onClick = { onLocomotiveClick(loco.id) }
                        )
                    }
                    items(uiState.rollingStock) { rs ->
                        AssetCard(
                            name = rs.name,
                            subtitle = rs.technicalSpecs.type,
                            status = rs.status,
                            icon = Icons.Default.DirectionsRailway,
                            onClick = { onRollingStockClick(rs.id) }
                        )
                    }
                    
                    if (uiState.locomotives.isEmpty() && uiState.rollingStock.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.no_assets_found))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun AssetFilterTabs(
    selectedFilter: AssetFilter,
    onFilterSelected: (AssetFilter) -> Unit
) {
    TabRow(selectedTabIndex = selectedFilter.ordinal) {
        AssetFilter.values().forEach { filter ->
            val label = when(filter) {
                AssetFilter.ALL -> stringResource(R.string.all_assets)
                AssetFilter.LOCOMOTIVES -> stringResource(R.string.locomotives)
                AssetFilter.ROLLING_STOCK -> stringResource(R.string.rolling_stock)
            }
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterChips(
    selectedStatus: AssetStatus?,
    onStatusSelected: (AssetStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = { Text(stringResource(R.string.all_status)) }
        )
        AssetStatus.values().forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = { Text(status.toLocalizedString()) }
            )
        }
    }
}

@Composable
fun AssetStatus.toLocalizedString(): String {
    return when(this) {
        AssetStatus.OPERATIONAL -> stringResource(R.string.operational)
        AssetStatus.MAINTENANCE -> stringResource(R.string.maintenance)
        AssetStatus.REPAIR -> stringResource(R.string.repair)
        AssetStatus.DECOMMISSIONED -> stringResource(R.string.decommissioned)
    }
}

@Composable
fun AssetCard(
    name: String,
    subtitle: String,
    status: AssetStatus,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            StatusBadge(status)
        }
    }
}

@Composable
fun StatusBadge(status: AssetStatus) {
    val color = when (status) {
        AssetStatus.OPERATIONAL -> MaterialTheme.colorScheme.primary
        AssetStatus.MAINTENANCE -> MaterialTheme.colorScheme.secondary
        AssetStatus.REPAIR -> MaterialTheme.colorScheme.error
        AssetStatus.DECOMMISSIONED -> MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.toLocalizedString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
