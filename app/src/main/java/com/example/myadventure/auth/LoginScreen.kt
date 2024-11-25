package com.example.myadventure.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Firebase 인증 UI를 제공하는 로그인 화면
 * @param authViewModel 인증 상태를 관리하는 ViewModel
 */
@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // 이름 입력 상태 추가

    val authState by authViewModel.authState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login", style = MaterialTheme.typography.titleLarge) }
            )
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
            // 이름 입력 필드 추가
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 이메일 입력 필드
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 비밀번호 입력 필드
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 회원가입 버튼
            OutlinedButton(
                onClick = { authViewModel.registerUser(email, password, name) }, // name 전달
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 상태 메시지
            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator() // 로딩 표시
                is AuthState.Success -> Text(
                    text = (authState as AuthState.Success).message,
                    color = MaterialTheme.colorScheme.primary
                )
                is AuthState.Error -> Text(
                    text = (authState as AuthState.Error).error,
                    color = MaterialTheme.colorScheme.error
                )
                AuthState.Idle -> {}
            }
        }
    }
}
