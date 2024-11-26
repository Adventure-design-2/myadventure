package com.example.myadventure.auth

import android.app.Activity
import android.content.Intent
import com.example.myadventure.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager(private val activity: Activity) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val oneTapClient: SignInClient = Identity.getSignInClient(activity)

    /**
     * 회원가입: Firebase Authentication에 사용자 등록 후 Firestore에 정보 저장
     */
    fun registerUser(
        email: String,
        password: String,
        additionalData: Map<String, Any>,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
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

    /**
     * 로그아웃: Firebase Authentication 세션 종료
     */
    fun logoutUser() {
        auth.signOut()
        db.terminate() // Firestore 세션 캐시 종료
    }

    /**
     * 현재 로그인된 사용자 이메일 반환
     */
    fun getCurrentUser(): String? {
        return auth.currentUser?.email
    }

    /**
     * Google 로그인 요청 Intent 생성
     */
    fun getGoogleSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id)) // Firebase의 웹 클라이언트 ID
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    /**
     * Google 로그인 결과 처리
     */
    fun handleGoogleSignInResult(
        data: Intent?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (data == null) {
            onFailure("로그인 결과가 없습니다.")
            return
        }

        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken

            if (idToken.isNullOrEmpty()) {
                onFailure("Google ID 토큰이 비어 있습니다.")
                return
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val additionalData = mapOf(
                                "name" to (user.displayName ?: "Anonymous User"),
                                "email" to (user.email ?: "Unknown"),
                                "profile_picture" to (user.photoUrl?.toString() ?: ""),
                                "created_at" to System.currentTimeMillis()
                            )
                            // Firestore 저장
                            db.collection("users").document(user.uid)
                                .set(additionalData)
                                .addOnSuccessListener {
                                    onSuccess("Google 로그인 성공: ${user.displayName}")
                                }
                                .addOnFailureListener { e ->
                                    onFailure("Firestore 데이터 저장 실패: ${e.message}")
                                }
                        } else {
                            onFailure("Firebase 사용자 정보를 가져올 수 없습니다.")
                        }
                    } else {
                        onFailure(task.exception?.message ?: "Google 인증 실패")
                    }
                }
        } catch (e: Exception) {
            onFailure("Google 로그인 처리 중 예외 발생: ${e.message}")
        }
    }

}
