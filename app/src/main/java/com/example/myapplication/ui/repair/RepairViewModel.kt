package com.example.myapplication.ui.repair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.*
import com.example.myapplication.data.repository.RailwayRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RepairUiState(
    val requests: List<RepairRequest> = emptyList(),
    val technicians: List<User> = emptyList(),
    val isLoading: Boolean = true
)

class RepairViewModel(private val repository: RailwayRepository) : ViewModel() {

    val uiState: StateFlow<RepairUiState> = combine(
        repository.getRepairRequests(),
        repository.getUsers().map { users -> users.filter { it.role == UserRole.TECHNICIAN } }
    ) { requests, techs ->
        RepairUiState(requests = requests, technicians = techs, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RepairUiState())

    fun updateStatus(requestId: String, status: RepairStatus) {
        viewModelScope.launch {
            repository.updateRepairRequestStatus(requestId, status)
        }
    }

    fun assignTechnician(requestId: String, technicianId: String) {
        viewModelScope.launch {
            repository.assignTechnician(requestId, technicianId)
        }
    }

    fun createRequest(assetId: String, assetType: AssetType, description: String, priority: RepairPriority) {
        val newRequest = RepairRequest(
            id = "rr${System.currentTimeMillis()}",
            assetId = assetId,
            assetType = assetType,
            description = description,
            status = RepairStatus.PENDING,
            priority = priority,
            technicianId = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.addRepairRequest(newRequest)
        }
    }
}
