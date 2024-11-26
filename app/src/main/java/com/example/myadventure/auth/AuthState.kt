package com.example.myadventure.auth

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}
