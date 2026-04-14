package com.example.myapplication.ui.repair

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.RepairPriority
import com.example.myapplication.data.model.RepairRequest
import com.example.myapplication.data.model.RepairStatus
import com.example.myapplication.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairRequestList(
    viewModel: RepairViewModel,
    userRole: UserRole,
    onCreateRequest: () -> Unit,
    onRequestClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.repair_requests)) }) },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN || userRole == UserRole.MANAGER) {
                FloatingActionButton(onClick = onCreateRequest) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_repair_request))
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.requests) { request ->
                    RepairRequestCard(
                        request = request,
                        onClick = { onRequestClick(request.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RepairRequestCard(request: RepairRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${stringResource(R.string.repair)} ${request.id.takeLast(4)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = request.description, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text(text = "${stringResource(R.string.asset_id)}: ${request.assetId}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                RepairStatusBadge(status = request.status)
                Spacer(modifier = Modifier.height(4.dp))
                PriorityBadge(priority = request.priority)
            }
        }
    }
}

@Composable
fun RepairStatusBadge(status: RepairStatus) {
    val color = when (status) {
        RepairStatus.PENDING -> MaterialTheme.colorScheme.secondary
        RepairStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        RepairStatus.COMPLETED -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        RepairStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.toLocalizedString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun RepairStatus.toLocalizedString(): String {
    return when(this) {
        RepairStatus.PENDING -> stringResource(R.string.pending)
        RepairStatus.IN_PROGRESS -> stringResource(R.string.in_progress_app)
        RepairStatus.COMPLETED -> stringResource(R.string.completed)
        RepairStatus.CANCELLED -> stringResource(R.string.cancelled)
    }
}

@Composable
fun PriorityBadge(priority: RepairPriority) {
    val color = when (priority) {
        RepairPriority.LOW -> MaterialTheme.colorScheme.outline
        RepairPriority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFFA000)
        RepairPriority.HIGH -> androidx.compose.ui.graphics.Color(0xFFF44336)
        RepairPriority.CRITICAL -> androidx.compose.ui.graphics.Color(0xFFB71C1C)
    }
    Text(
        text = priority.toLocalizedString(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RepairPriority.toLocalizedString(): String {
    return when(this) {
        RepairPriority.LOW -> stringResource(R.string.low)
        RepairPriority.MEDIUM -> stringResource(R.string.medium)
        RepairPriority.HIGH -> stringResource(R.string.high)
        RepairPriority.CRITICAL -> stringResource(R.string.critical)
    }
}
