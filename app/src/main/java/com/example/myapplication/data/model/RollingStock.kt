package com.example.myapplication.data.model

data class RollingStockSpecs(
    val capacityTons: Double,
    val lengthMeters: Double,
    val type: String // e.g., Freight, Passenger, Tanker
)

data class RollingStock(
    val id: String,
    val name: String,
    val technicalSpecs: RollingStockSpecs,
    val status: AssetStatus,
    val imageUrl: String? = null,
    val lastMaintenanceDate: String? = null
)
