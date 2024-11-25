package com.example.myadventure.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * 회원가입: Firebase Authentication에 사용자 등록 후 Firestore에 정보 저장
     */
    fun registerUser(
        email: String,
        password: String,
        additionalData: Map<String, Any>, // 추가 데이터
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Firestore에 사용자 정보 저장
                        db.collection("users").document(userId)
                            .set(additionalData)
                            .addOnSuccessListener {
                                onSuccess("회원가입 성공: $email")
                            }
                            .addOnFailureListener { e ->
                                onFailure("데이터베이스 저장 실패: ${e.message}")
                            }
                    } else {
                        onFailure("사용자 ID 생성 실패")
                    }
                } else {
                    onFailure(task.exception?.message ?: "회원가입 실패")
                }
            }
    }

    /**
     * 로그인: Firebase Authentication을 통해 인증 후 Firestore에서 추가 검증
     */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Firestore에서 사용자 데이터 확인
                        db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    onSuccess("로그인 성공: $email")
                                } else {
                                    onFailure("사용자 데이터베이스 기록 없음")
                                }
                            }
                            .addOnFailureListener { e ->
                                onFailure("데이터베이스 확인 실패: ${e.message}")
                            }
                    } else {
                        onFailure("사용자 ID 확인 실패")
                    }
                } else {
                    onFailure(task.exception?.message ?: "로그인 실패")
                }
            }
    }
    // 로그아웃
    fun logoutUser() {
        auth.signOut()
    }

    // 현재 사용자 확인
    fun getCurrentUser(): String? {
        return auth.currentUser?.email
    }
}
