package com.example.myapplication.ui.repair

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.RepairStatus
import com.example.myapplication.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairDetailScreen(
    requestId: String,
    viewModel: RepairViewModel,
    userRole: UserRole,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val request = uiState.requests.find { it.id == requestId }
    var showAssignDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.repair_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (request == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_assets_found))
            }
        } else {
            Column(
                modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("${stringResource(R.string.asset_id)}: ${request.assetId}", style = MaterialTheme.typography.headlineSmall)
                Text("${stringResource(R.string.description)}: ${request.description}", style = MaterialTheme.typography.bodyLarge)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${stringResource(R.string.status)}: ")
                    RepairStatusBadge(status = request.status)
                }

                Text("${stringResource(R.string.technician)}: ${uiState.technicians.find { it.id == request.technicianId }?.name ?: stringResource(R.string.unassigned)}")

                // Technician can update status
                if (userRole == UserRole.TECHNICIAN || userRole == UserRole.ADMIN) {
                    Text(stringResource(R.string.update_status), style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RepairStatus.values().forEach { status ->
                            Button(
                                onClick = { viewModel.updateStatus(requestId, status) },
                                enabled = request.status != status
                            ) {
                                Text(status.toLocalizedString())
                            }
                        }
                    }
                }

                // Manager can assign technician
                if (userRole == UserRole.MANAGER || userRole == UserRole.ADMIN) {
                    Button(onClick = { showAssignDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.assign_technician))
                    }
                }
            }
        }
    }

    if (showAssignDialog) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text(stringResource(R.string.assign_technician)) },
            text = {
                Column {
                    uiState.technicians.forEach { tech ->
                        TextButton(
                            onClick = {
                                viewModel.assignTechnician(requestId, tech.id)
                                showAssignDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tech.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
