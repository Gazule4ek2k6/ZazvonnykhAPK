package com.example.myapplication.ui.fleet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.Locomotive
import com.example.myapplication.data.model.RollingStock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocomotiveDetailScreen(
    assetId: String,
    viewModel: FleetViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val locomotive = uiState.locomotives.find { it.id == assetId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(locomotive?.name ?: stringResource(R.string.locomotive_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (locomotive == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_assets_found))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssetHeader(name = locomotive.name, status = locomotive.status)
                
                DetailSection(title = stringResource(R.string.general_info), icon = Icons.Default.Info) {
                    DetailRow(label = stringResource(R.string.model), value = locomotive.model)
                    DetailRow(label = stringResource(R.string.id), value = locomotive.id)
                    DetailRow(label = stringResource(R.string.last_maintenance), value = locomotive.lastMaintenanceDate ?: stringResource(R.string.unassigned))
                }

                DetailSection(title = stringResource(R.string.tech_specs), icon = Icons.Default.Settings) {
                    DetailRow(label = stringResource(R.string.power), value = "${locomotive.technicalSpecs.powerHp} HP")
                    DetailRow(label = stringResource(R.string.weight), value = "${locomotive.technicalSpecs.weightTons} T")
                    DetailRow(label = stringResource(R.string.max_speed), value = "${locomotive.technicalSpecs.maxSpeedKph} km/h")
                    DetailRow(label = stringResource(R.string.fuel_type), value = locomotive.technicalSpecs.fuelType)
                }

                Button(
                    onClick = { /* TODO: Create Repair Request */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.create_repair_request))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RollingStockDetailScreen(
    assetId: String,
    viewModel: FleetViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rollingStock = uiState.rollingStock.find { it.id == assetId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rollingStock?.name ?: stringResource(R.string.rolling_stock_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (rollingStock == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_assets_found))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssetHeader(name = rollingStock.name, status = rollingStock.status)

                DetailSection(title = stringResource(R.string.general_info), icon = Icons.Default.Info) {
                    DetailRow(label = stringResource(R.string.type), value = rollingStock.technicalSpecs.type)
                    DetailRow(label = stringResource(R.string.id), value = rollingStock.id)
                    DetailRow(label = stringResource(R.string.last_maintenance), value = rollingStock.lastMaintenanceDate ?: stringResource(R.string.unassigned))
                }

                DetailSection(title = stringResource(R.string.tech_specs), icon = Icons.Default.Settings) {
                    DetailRow(label = stringResource(R.string.capacity), value = "${rollingStock.technicalSpecs.capacityTons} T")
                    DetailRow(label = stringResource(R.string.length), value = "${rollingStock.technicalSpecs.lengthMeters} m")
                }

                Button(
                    onClick = { /* TODO: Create Repair Request */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.create_repair_request))
                }
            }
        }
    }
}

@Composable
fun AssetHeader(name: String, status: com.example.myapplication.data.model.AssetStatus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        StatusBadge(status = status)
    }
}

@Composable
fun DetailSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
