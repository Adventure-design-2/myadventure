package com.example.myadventure

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myadventure.auth.AuthState
import com.example.myadventure.auth.AuthViewModel
import com.example.myadventure.auth.AuthViewModelFactory
import com.example.myadventure.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 로그인 상태 확인 및 화면 전환
        checkLoginState(navController)
    }

    /**
     * 로그인 상태를 확인하고 화면을 전환
     */
    private fun checkLoginState(navController: androidx.navigation.NavController) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // 로그인이 안 된 상태 -> LoginScreen으로 이동
            navController.navigate(R.id.navigation_login)
        } else {
            // 로그인이 된 상태 -> HomeScreen으로 이동
            navController.navigate(R.id.navigation_home)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { authState ->
                    when (authState) {
                        is AuthState.Idle -> {
                            // 초기 상태
                        }
                        is AuthState.Loading -> {
                            showLoading(true)
                        }
                        is AuthState.Success -> {
                            showLoading(false)
                            showMessage(authState.message)
                        }
                        is AuthState.Error -> {
                            showLoading(false)
                            showMessage(authState.error)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // 로딩 상태를 보여줄 수 있는 UI 처리 (예: ProgressBar 표시)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showLoginScreen() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_login) {
            popUpTo(R.id.nav_graph) { inclusive = true } // 이전 화면 제거
        }
    }

    private fun showHomeScreen() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home) {
            popUpTo(R.id.nav_graph) { inclusive = true } // 이전 화면 제거
        }
    }

}

