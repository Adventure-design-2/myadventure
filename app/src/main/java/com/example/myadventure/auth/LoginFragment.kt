package com.example.myadventure.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.compose.ui.platform.ComposeView
import com.example.myadventure.ui.theme.MyAdventureTheme

class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MyAdventureTheme { // 테마 적용
                    LoginScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}
