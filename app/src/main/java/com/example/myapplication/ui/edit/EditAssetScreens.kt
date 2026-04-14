package com.example.myapplication.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.fleet.FleetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLocomotiveScreen(
    assetId: String,
    editViewModel: EditAssetViewModel,
    fleetViewModel: FleetViewModel,
    onBackClick: () -> Unit
) {
    val fleetUiState by fleetViewModel.uiState.collectAsState()
    val locomotive = fleetUiState.locomotives.find { it.id == assetId }

    if (locomotive != null) {
        var name by remember { mutableStateOf(locomotive.name) }
        var model by remember { mutableStateOf(locomotive.model) }
        var powerHp by remember { mutableStateOf(locomotive.technicalSpecs.powerHp.toString()) }
        var weightTons by remember { mutableStateOf(locomotive.technicalSpecs.weightTons.toString()) }
        var maxSpeedKph by remember { mutableStateOf(locomotive.technicalSpecs.maxSpeedKph.toString()) }
        var fuelType by remember { mutableStateOf(locomotive.technicalSpecs.fuelType) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${stringResource(R.string.edit)}: ${locomotive.name}") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            editViewModel.updateLocomotive(
                                id = locomotive.id,
                                name = name,
                                model = model,
                                power = powerHp.toIntOrNull() ?: locomotive.technicalSpecs.powerHp,
                                weight = weightTons.toDoubleOrNull() ?: locomotive.technicalSpecs.weightTons,
                                speed = maxSpeedKph.toIntOrNull() ?: locomotive.technicalSpecs.maxSpeedKph,
                                fuel = fuelType,
                                status = locomotive.status
                            )
                            onBackClick()
                        }) {
                            Icon(Icons.Default.Save, contentDescription = stringResource(R.string.save_changes))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.general_info)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text(stringResource(R.string.model)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = powerHp, onValueChange = { powerHp = it }, label = { Text(stringResource(R.string.power)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weightTons, onValueChange = { weightTons = it }, label = { Text(stringResource(R.string.weight)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = maxSpeedKph, onValueChange = { maxSpeedKph = it }, label = { Text(stringResource(R.string.max_speed)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fuelType, onValueChange = { fuelType = it }, label = { Text(stringResource(R.string.fuel_type)) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRollingStockScreen(
    assetId: String,
    editViewModel: EditAssetViewModel,
    fleetViewModel: FleetViewModel,
    onBackClick: () -> Unit
) {
    val fleetUiState by fleetViewModel.uiState.collectAsState()
    val rollingStock = fleetUiState.rollingStock.find { it.id == assetId }

    if (rollingStock != null) {
        var name by remember { mutableStateOf(rollingStock.name) }
        var type by remember { mutableStateOf(rollingStock.technicalSpecs.type) }
        var capacityTons by remember { mutableStateOf(rollingStock.technicalSpecs.capacityTons.toString()) }
        var lengthMeters by remember { mutableStateOf(rollingStock.technicalSpecs.lengthMeters.toString()) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${stringResource(R.string.edit)}: ${rollingStock.name}") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            editViewModel.updateRollingStock(
                                id = rollingStock.id,
                                name = name,
                                capacity = capacityTons.toDoubleOrNull() ?: rollingStock.technicalSpecs.capacityTons,
                                length = lengthMeters.toDoubleOrNull() ?: rollingStock.technicalSpecs.lengthMeters,
                                type = type,
                                status = rollingStock.status
                            )
                            onBackClick()
                        }) {
                            Icon(Icons.Default.Save, contentDescription = stringResource(R.string.save_changes))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.general_info)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text(stringResource(R.string.type)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = capacityTons, onValueChange = { capacityTons = it }, label = { Text(stringResource(R.string.capacity)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lengthMeters, onValueChange = { lengthMeters = it }, label = { Text(stringResource(R.string.length)) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
