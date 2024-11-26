package com.example.myadventure.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel()) {
    // 사용자 입력 상태 관리
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("로그인") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 로그인 상태 확인
            if (authState is AuthState.Success) {
                Text("로그인 성공!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { authViewModel.logoutUser() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("로그아웃")
                }
            } else {
                // 이메일 및 비밀번호 입력
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 로그인 버튼
                Button(
                    onClick = { authViewModel.loginUser(email, password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("로그인")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 회원가입 버튼
                OutlinedButton(
                    onClick = { authViewModel.registerUser(email, password, "사용자 이름") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("회원가입")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}
