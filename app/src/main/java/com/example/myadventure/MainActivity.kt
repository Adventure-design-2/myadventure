package com.example.myadventure

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myadventure.auth.AuthState
import com.example.myadventure.auth.AuthViewModel
import com.example.myadventure.auth.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myadventure.databinding.ActivityMainBinding
import com.example.myadventure.auth.*
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ViewModel 초기화
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthManager())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 인증 상태 관찰
        observeAuthState()
    }

    /**
     * AuthViewModel의 상태를 관찰하여 UI에 반영
     */
    private fun observeAuthState() {
        lifecycleScope.launchWhenStarted {
            authViewModel.authState.collect { authState ->
                when (authState) {
                    is AuthState.Idle -> {
                        // 초기 상태: 아무 작업도 하지 않음
                    }
                    is AuthState.Loading -> {
                        // 로딩 상태 처리 (예: 로딩 스피너 표시)
                        showLoading(true)
                    }
                    is AuthState.Success -> {
                        // 인증 성공 처리
                        showLoading(false)
                        showMessage(authState.message)
                    }
                    is AuthState.Error -> {
                        // 인증 실패 처리
                        showLoading(false)
                        showMessage(authState.error)
                    }
                }
            }
        }
    }

    /**
     * 로딩 스피너 표시/숨기기
     */
    private fun showLoading(isLoading: Boolean) {
        // 로딩 스피너를 표시하거나 숨기는 로직 구현
        // 예: ProgressBar 사용
    }

    /**
     * 사용자에게 메시지 표시
     */
    private fun showMessage(message: String) {
        // 예: Snackbar 또는 Toast를 사용해 메시지 표시
        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
