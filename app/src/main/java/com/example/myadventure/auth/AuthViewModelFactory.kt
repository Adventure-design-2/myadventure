package com.example.myadventure.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(AuthManager(activity)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

