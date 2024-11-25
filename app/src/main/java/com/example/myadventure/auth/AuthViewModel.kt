package com.example.myadventure.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Firebase Authentication과 Firestore를 관리하는 ViewModel
 */
class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    // 인증 상태를 관리하는 StateFlow
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    /**
     * 회원가입: Firebase Authentication 및 Firestore에 사용자 정보 저장
     */
    fun registerUser(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading // 로딩 상태로 변경
        val additionalData = mapOf(
            "name" to name,
            "email" to email,
            "created_at" to System.currentTimeMillis()
        )
        authManager.registerUser(
            email = email,
            password = password,
            additionalData = additionalData,
            onSuccess = {
                _authState.value = AuthState.Success("회원가입 성공: $email")
            },
            onFailure = {
                _authState.value = AuthState.Error(it)
            }
        )
    }

    /**
     * 로그인: Firebase Authentication 및 Firestore에서 사용자 확인
     */
    fun loginUser(email: String, password: String) {
        _authState.value = AuthState.Loading // 로딩 상태로 변경
        authManager.loginUser(
            email = email,
            password = password,
            onSuccess = {
                _authState.value = AuthState.Success("로그인 성공: $email")
            },
            onFailure = {
                _authState.value = AuthState.Error(it)
            }
        )
    }

    /**
     * 로그아웃: 인증 상태를 초기화
     */
    fun logoutUser() {
        authManager.logoutUser()
        _authState.value = AuthState.Idle // 초기 상태로 변경
    }
}

/**
 * 인증 상태를 나타내는 sealed class
 */
sealed class AuthState {
    data object Idle : AuthState() // 초기 상태
    data object Loading : AuthState() // 로딩 중
    data class Success(val message: String) : AuthState() // 성공 상태
    data class Error(val error: String) : AuthState() // 실패 상태
}
