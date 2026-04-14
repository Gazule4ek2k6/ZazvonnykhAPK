package com.example.myapplication.ui.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.AssetStatus
import com.example.myapplication.data.model.Locomotive
import com.example.myapplication.data.model.RollingStock
import com.example.myapplication.data.repository.RailwayRepository
import kotlinx.coroutines.flow.*

enum class AssetFilter {
    ALL, LOCOMOTIVES, ROLLING_STOCK
}

data class FleetUiState(
    val locomotives: List<Locomotive> = emptyList(),
    val rollingStock: List<RollingStock> = emptyList(),
    val searchQuery: String = "",
    val assetFilter: AssetFilter = AssetFilter.ALL,
    val statusFilter: AssetStatus? = null,
    val isLoading: Boolean = true
)

class FleetViewModel(private val repository: RailwayRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _assetFilter = MutableStateFlow(AssetFilter.ALL)
    val assetFilter = _assetFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<AssetStatus?>(null)
    val statusFilter = _statusFilter.asStateFlow()

    val uiState: StateFlow<FleetUiState> = combine(
        repository.getLocomotives(),
        repository.getRollingStock(),
        _searchQuery,
        _assetFilter,
        _statusFilter
    ) { locos, rolling, query, assetF, statusF ->
        val filteredLocos = locos.filter { loco ->
            (assetF == AssetFilter.ALL || assetF == AssetFilter.LOCOMOTIVES) &&
            (statusF == null || loco.status == statusF) &&
            (query.isEmpty() || loco.name.contains(query, ignoreCase = true) || loco.model.contains(query, ignoreCase = true))
        }
        val filteredRolling = rolling.filter { rs ->
            (assetF == AssetFilter.ALL || assetF == AssetFilter.ROLLING_STOCK) &&
            (statusF == null || rs.status == statusF) &&
            (query.isEmpty() || rs.name.contains(query, ignoreCase = true) || rs.technicalSpecs.type.contains(query, ignoreCase = true))
        }
        FleetUiState(
            locomotives = filteredLocos,
            rollingStock = filteredRolling,
            searchQuery = query,
            assetFilter = assetF,
            statusFilter = statusF,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FleetUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateAssetFilter(filter: AssetFilter) {
        _assetFilter.value = filter
    }

    fun updateStatusFilter(status: AssetStatus?) {
        _statusFilter.value = status
    }
}
