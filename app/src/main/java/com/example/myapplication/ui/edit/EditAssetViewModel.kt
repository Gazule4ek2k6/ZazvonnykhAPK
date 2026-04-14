package com.example.myapplication.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.*
import com.example.myapplication.data.repository.RailwayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditAssetViewModel(private val repository: RailwayRepository) : ViewModel() {

    fun updateLocomotive(
        id: String,
        name: String,
        model: String,
        power: Int,
        weight: Double,
        speed: Int,
        fuel: String,
        status: AssetStatus
    ) {
        val updated = Locomotive(
            id = id,
            name = name,
            model = model,
            technicalSpecs = LocomotiveSpecs(power, weight, speed, fuel),
            status = status
        )
        viewModelScope.launch {
            repository.updateLocomotive(updated)
        }
    }

    fun updateRollingStock(
        id: String,
        name: String,
        capacity: Double,
        length: Double,
        type: String,
        status: AssetStatus
    ) {
        val updated = RollingStock(
            id = id,
            name = name,
            technicalSpecs = RollingStockSpecs(capacity, length, type),
            status = status
        )
        viewModelScope.launch {
            repository.updateRollingStock(updated)
        }
    }
}
