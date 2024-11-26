package com.example.myadventure.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*

data class UserProfile(
    val name: String,
    val email: String,
    val profileImageUrl: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri: StateFlow<String?> get() = _imageUri

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    init {
        fetchUserProfile()
    }

    /**
     * Firestore에서 사용자 프로필 정보를 가져옵니다.
     */
    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Unknown"
                    val email = document.getString("email") ?: "Unknown"
                    val profileImageUrl = document.getString("profileImageUrl")
                    _userProfile.update { UserProfile(name, email, profileImageUrl) }
                    _imageUri.update { profileImageUrl }
                }
            }
            .addOnFailureListener {
                // 실패 시 로그 또는 에러 처리
            }
    }

    /**
     * Firebase Storage에 프로필 이미지를 업로드하고 Firestore에 URL 저장
     */
    fun uploadProfileImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Firestore에 이미지 URL 저장
                    db.collection("users").document(userId)
                        .update("profileImageUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            _imageUri.update { downloadUri.toString() }
                        }
                }
            }
            .addOnFailureListener {
                // 업로드 실패 처리
            }
    }

    /**
     * Firebase 인증 로그아웃 처리
     */
    fun logout() {
        auth.signOut()
        _userProfile.update { null }
    }
}
