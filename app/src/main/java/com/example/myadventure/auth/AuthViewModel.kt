package com.example.myadventure.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.myadventure.auth.AuthState.Idle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Firebase Authentication과 Firestore를 관리하는 ViewModel
 */
class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    // 인증 상태를 관리하는 StateFlow
    private val _authState = MutableStateFlow<AuthState>(Idle)
    val authState: StateFlow<AuthState> get() = _authState

    /**
     * 회원가입: Firebase Authentication 및 Firestore에 사용자 정보 저장
     */
    fun registerUser(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading
        authManager.registerUser(
            email = email,
            password = password,
            additionalData = mapOf("name" to name),
            onSuccess = {
                _authState.value = AuthState.Success("회원가입 성공")
            },
            onFailure = {
                _authState.value = AuthState.Error("회원가입 실패: $it")
            }
        )
    }

    fun loginUser(email: String, password: String) {
        _authState.value = AuthState.Loading
        authManager.loginUser(
            email = email,
            password = password,
            onSuccess = {
                _authState.value = AuthState.Success("로그인 성공")
            },
            onFailure = {
                _authState.value = AuthState.Error("로그인 실패: $it")
            }
        )
    }


    /**
     * 로그아웃: 인증 상태를 초기화
     */
    fun logoutUser() {
        authManager.logoutUser()
        _authState.value = Idle // 초기 상태로 변경
    }

    /**
     * Google 로그인 처리
     */
    fun loginWithGoogle(data: Intent?, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (data == null) {
            onFailure("Google 로그인 데이터가 비어 있습니다.")
            return
        }

        authManager.handleGoogleSignInResult(
            data = data,
            onSuccess = { message -> onSuccess(message) },
            onFailure = { error -> onFailure(error) }
        )
    }
}

