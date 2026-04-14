package com.example.myapplication.ui.repair

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetType
import com.example.myapplication.data.model.RepairPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRepairRequestScreen(
    viewModel: RepairViewModel,
    onBackClick: () -> Unit
) {
    var assetId by remember { mutableStateOf("") }
    var assetType by remember { mutableStateOf(AssetType.LOCOMOTIVE) }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(RepairPriority.MEDIUM) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_repair_request)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = assetId,
                onValueChange = { assetId = it },
                label = { Text(stringResource(R.string.asset_id)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text("${stringResource(R.string.type)}:", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = assetType == AssetType.LOCOMOTIVE,
                    onClick = { assetType = AssetType.LOCOMOTIVE },
                    label = { Text(stringResource(R.string.locomotives)) }
                )
                FilterChip(
                    selected = assetType == AssetType.ROLLING_STOCK,
                    onClick = { assetType = AssetType.ROLLING_STOCK },
                    label = { Text(stringResource(R.string.rolling_stock)) }
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("${stringResource(R.string.priority)}:", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RepairPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.toLocalizedString()) }
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.createRequest(assetId, assetType, description, priority)
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = assetId.isNotEmpty() && description.isNotEmpty()
            ) {
                Text(stringResource(R.string.submit_request))
            }
        }
    }
}
