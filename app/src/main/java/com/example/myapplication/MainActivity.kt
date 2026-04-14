package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.model.AssetType
import com.example.myapplication.data.model.User
import com.example.myapplication.data.model.UserRole
import com.example.myapplication.data.repository.RailwayRepository
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.edit.*
import com.example.myapplication.ui.fleet.FleetDashboard
import com.example.myapplication.ui.fleet.FleetViewModel
import com.example.myapplication.ui.fleet.LocomotiveDetailScreen
import com.example.myapplication.ui.fleet.RollingStockDetailScreen
import com.example.myapplication.ui.repair.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = RailwayRepository()

        setContent {
            MyApplicationTheme {
                RailwayApp(repository)
            }
        }
    }
}

@Composable
fun RailwayApp(repository: RailwayRepository) {
    val navController = rememberNavController()
    val fleetViewModel = remember { FleetViewModel(repository) }
    val repairViewModel = remember { RepairViewModel(repository) }
    val authViewModel = remember { AuthViewModel(repository) }
    val editViewModel = remember { EditAssetViewModel(repository) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val users by repository.getUsers().collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Train, contentDescription = null) },
                    label = { Text(stringResource(R.string.fleet_inventory)) },
                    selected = currentDestination?.hierarchy?.any { it.route?.startsWith("fleet") == true || it.route?.startsWith("locomotive") == true || it.route?.startsWith("rolling_stock") == true || it.route?.startsWith("edit") == true } == true,
                    onClick = {
                        navController.navigate("fleet_root") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    label = { Text(stringResource(R.string.repair_requests)) },
                    selected = currentDestination?.hierarchy?.any { it.route?.startsWith("repair") == true || it.route?.startsWith("create_repair") == true } == true,
                    onClick = {
                        navController.navigate("repair_root") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.profile)) },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "fleet_root", modifier = Modifier.padding(paddingValues)) {
            composable("fleet_root") {
                FleetDashboard(
                    viewModel = fleetViewModel,
                    onLocomotiveClick = { id -> navController.navigate("locomotive_detail/$id") },
                    onRollingStockClick = { id -> navController.navigate("rolling_stock_detail/$id") }
                )
            }
            composable("locomotive_detail/{assetId}") { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                LocomotiveDetailWithActions(
                    assetId = assetId,
                    fleetViewModel = fleetViewModel,
                    userRole = currentUser?.role ?: UserRole.TECHNICIAN,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { navController.navigate("edit_locomotive/$assetId") },
                    onCreateRepairClick = { navController.navigate("create_repair/$assetId/LOCOMOTIVE") }
                )
            }
            composable("rolling_stock_detail/{assetId}") { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                RollingStockDetailWithActions(
                    assetId = assetId,
                    fleetViewModel = fleetViewModel,
                    userRole = currentUser?.role ?: UserRole.TECHNICIAN,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { navController.navigate("edit_rolling_stock/$assetId") },
                    onCreateRepairClick = { navController.navigate("create_repair/$assetId/ROLLING_STOCK") }
                )
            }
            composable("edit_locomotive/{assetId}") { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                EditLocomotiveScreen(assetId, editViewModel, fleetViewModel, onBackClick = { navController.popBackStack() })
            }
            composable("edit_rolling_stock/{assetId}") { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                EditRollingStockScreen(assetId, editViewModel, fleetViewModel, onBackClick = { navController.popBackStack() })
            }
            composable("repair_root") {
                RepairRequestList(
                    viewModel = repairViewModel,
                    userRole = currentUser?.role ?: UserRole.TECHNICIAN,
                    onCreateRequest = { /* This is handled per asset */ },
                    onRequestClick = { id -> navController.navigate("repair_detail/$id") }
                )
            }
            composable("repair_detail/{requestId}") { backStackEntry ->
                val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
                RepairDetailScreen(
                    requestId = requestId,
                    viewModel = repairViewModel,
                    userRole = currentUser?.role ?: UserRole.TECHNICIAN,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("create_repair/{assetId}/{assetType}") {
                CreateRepairRequestScreen(repairViewModel, onBackClick = { navController.popBackStack() })
            }
            composable("profile") {
                ProfileScreen(currentUser, users, onUserSelect = { authViewModel.setCurrentUser(it) })
            }
        }
    }
}

@Composable
fun ProfileScreen(currentUser: User?, allUsers: List<User>, onUserSelect: (User) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("${stringResource(R.string.current_user)}: ${currentUser?.name} (${currentUser?.role})", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.switch_role_app), style = MaterialTheme.typography.titleMedium)
        allUsers.forEach { user ->
            OutlinedButton(
                onClick = { onUserSelect(user) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                enabled = currentUser?.id != user.id
            ) {
                Text("${user.name} - ${user.role}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocomotiveDetailWithActions(
    assetId: String,
    fleetViewModel: FleetViewModel,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCreateRepairClick: () -> Unit
) {
    val uiState by fleetViewModel.uiState.collectAsState()
    val locomotive = uiState.locomotives.find { it.id == assetId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(locomotive?.name ?: stringResource(R.string.locomotives)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
                actions = {
                    if (userRole == UserRole.ADMIN) {
                        IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit)) }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                LocomotiveDetailScreen(assetId, fleetViewModel, onBackClick)
                Button(onClick = onCreateRepairClick, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(stringResource(R.string.create_repair_request))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RollingStockDetailWithActions(
    assetId: String,
    fleetViewModel: FleetViewModel,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCreateRepairClick: () -> Unit
) {
    val uiState by fleetViewModel.uiState.collectAsState()
    val rs = uiState.rollingStock.find { it.id == assetId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rs?.name ?: stringResource(R.string.rolling_stock)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
                actions = {
                    if (userRole == UserRole.ADMIN) {
                        IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit)) }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                RollingStockDetailScreen(assetId, fleetViewModel, onBackClick)
                Button(onClick = onCreateRepairClick, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(stringResource(R.string.create_repair_request))
                }
            }
        }
    }
}
