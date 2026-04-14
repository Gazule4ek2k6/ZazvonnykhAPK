package com.example.myapplication.data.repository

import com.example.myapplication.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class RailwayRepository {
    private val _locomotives = MutableStateFlow<List<Locomotive>>(emptyList())
    private val _rollingStock = MutableStateFlow<List<RollingStock>>(emptyList())
    private val _repairRequests = MutableStateFlow<List<RepairRequest>>(emptyList())
    private val _users = MutableStateFlow<List<User>>(emptyList())

    init {
        generateMockData()
    }

    fun getLocomotives(): Flow<List<Locomotive>> = _locomotives.asStateFlow()
    fun getRollingStock(): Flow<List<RollingStock>> = _rollingStock.asStateFlow()
    fun getRepairRequests(): Flow<List<RepairRequest>> = _repairRequests.asStateFlow()
    fun getUsers(): Flow<List<User>> = _users.asStateFlow()

    fun getLocomotiveById(id: String): Flow<Locomotive?> = 
        _locomotives.map { list -> list.find { it.id == id } }

    fun getRollingStockById(id: String): Flow<RollingStock?> = 
        _rollingStock.map { list -> list.find { it.id == id } }

    fun getRepairRequestById(id: String): Flow<RepairRequest?> = 
        _repairRequests.map { list -> list.find { it.id == id } }

    fun updateRepairRequestStatus(id: String, status: RepairStatus) {
        val currentList = _repairRequests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = currentList[index].copy(status = status, updatedAt = System.currentTimeMillis())
            currentList[index] = updated
            _repairRequests.value = currentList
        }
    }

    fun assignTechnician(requestId: String, technicianId: String) {
        val currentList = _repairRequests.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val updated = currentList[index].copy(technicianId = technicianId, updatedAt = System.currentTimeMillis())
            currentList[index] = updated
            _repairRequests.value = currentList
        }
    }

    fun updateLocomotive(updatedLoco: Locomotive) {
        val currentList = _locomotives.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedLoco.id }
        if (index != -1) {
            currentList[index] = updatedLoco
            _locomotives.value = currentList
        }
    }

    fun updateRollingStock(updatedRS: RollingStock) {
        val currentList = _rollingStock.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedRS.id }
        if (index != -1) {
            currentList[index] = updatedRS
            _rollingStock.value = currentList
        }
    }

    fun addRepairRequest(request: RepairRequest) {
        val currentList = _repairRequests.value.toMutableList()
        currentList.add(request)
        _repairRequests.value = currentList
    }

    private fun generateMockData() {
        _users.value = listOf(
            User("u1", "Иван Админ", UserRole.ADMIN, "admin@railway.com"),
            User("u2", "Борис Техник", UserRole.TECHNICIAN, "bob@railway.com"),
            User("u3", "Алиса Менеджер", UserRole.MANAGER, "alice@railway.com"),
            User("u4", "Чарли Техник", UserRole.TECHNICIAN, "charlie@railway.com")
        )

        _locomotives.value = listOf(
            Locomotive("l1", "Гром", "ТЭП70", LocomotiveSpecs(4000, 135.0, 160, "Дизель"), AssetStatus.OPERATIONAL, lastMaintenanceDate = "10.01.2024"),
            Locomotive("l2", "Железный конь", "2ТЭ25КМ", LocomotiveSpecs(7200, 288.0, 120, "Дизель"), AssetStatus.REPAIR, lastMaintenanceDate = "15.02.2024"),
            Locomotive("l3", "Молния", "Сапсан (Velaro RUS)", LocomotiveSpecs(11000, 667.0, 250, "Электро"), AssetStatus.MAINTENANCE, lastMaintenanceDate = "05.03.2024")
        )

        _rollingStock.value = listOf(
            RollingStock("rs1", "Крытый вагон А1", RollingStockSpecs(68.0, 14.7, "Грузовой"), AssetStatus.OPERATIONAL, lastMaintenanceDate = "12.12.2023"),
            RollingStock("rs2", "Цистерна Т2", RollingStockSpecs(66.0, 12.0, "Цистерна"), AssetStatus.OPERATIONAL, lastMaintenanceDate = "20.01.2024"),
            RollingStock("rs3", "Пассажирский С3", RollingStockSpecs(54.0, 24.5, "Пассажирский"), AssetStatus.REPAIR, lastMaintenanceDate = "01.02.2024")
        )

        _repairRequests.value = listOf(
            RepairRequest("rr1", "l2", AssetType.LOCOMOTIVE, "Перегрев двигателя", RepairStatus.IN_PROGRESS, RepairPriority.HIGH, "u2", System.currentTimeMillis() - 86400000, System.currentTimeMillis()),
            RepairRequest("rr2", "rs3", AssetType.ROLLING_STOCK, "Неисправность колесной пары", RepairStatus.PENDING, RepairPriority.CRITICAL, null, System.currentTimeMillis() - 3600000, System.currentTimeMillis())
        )
    }
}
