package com.example.myapplication.data.model

enum class RepairPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class RepairStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class AssetType {
    LOCOMOTIVE, ROLLING_STOCK
}

data class RepairRequest(
    val id: String,
    val assetId: String,
    val assetType: AssetType,
    val description: String,
    val status: RepairStatus,
    val priority: RepairPriority,
    val technicianId: String?,
    val createdAt: Long,
    val updatedAt: Long
)
