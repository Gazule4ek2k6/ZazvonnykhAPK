package com.example.myapplication.ui.auth

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.User
import com.example.myapplication.data.model.UserRole
import com.example.myapplication.data.repository.RailwayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(private val repository: RailwayRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Default to Admin for testing/prototyping, but would normally be null
        login("u1") 
    }

    fun login(userId: String) {
        // Simple mock login: find user in repository
        // In a real app, this would involve authentication
    }
    
    // For prototyping purposes, we'll allow switching roles easily
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }
}
