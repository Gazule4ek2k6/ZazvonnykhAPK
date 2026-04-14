package com.example.myapplication.data.model

enum class AssetStatus {
    OPERATIONAL, MAINTENANCE, REPAIR, DECOMMISSIONED
}

data class LocomotiveSpecs(
    val powerHp: Int,
    val weightTons: Double,
    val maxSpeedKph: Int,
    val fuelType: String
)

data class Locomotive(
    val id: String,
    val name: String,
    val model: String,
    val technicalSpecs: LocomotiveSpecs,
    val status: AssetStatus,
    val imageUrl: String? = null,
    val lastMaintenanceDate: String? = null
)
