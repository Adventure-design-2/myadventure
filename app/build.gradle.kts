plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myadventure"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myadventure"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    // Jetpack Compose 활성화
    buildFeatures {
        compose = true // Compose 사용 여부를 설정
    }

    // Compose 관련 옵션
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Compose 컴파일러의 최신 버전 지정
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //firebase 기본 세팅
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    // 파이어 베이스 - login, db
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)

    implementation(platform(libs.androidx.compose.bom)) // Compose BOM

    implementation(libs.androidx.ui) // Compose UI
    implementation(libs.androidx.material3) // Material Design 3
    implementation(libs.androidx.ui.tooling.preview) // UI 미리보기
    implementation(libs.androidx.lifecycle.runtime.compose) // LiveData 연동
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel 연동
    implementation(libs.androidx.navigation.compose) // Navigation
    implementation(libs.androidx.activity.compose) // Activity에서 Compose 사용
    implementation(libs.androidx.ui.text) // 텍스트
    implementation(libs.androidx.compose.ui.ui) // Compose UI
    debugImplementation(libs.androidx.ui.tooling) // UI 디버깅 도구
    debugImplementation(libs.androidx.ui.test.manifest) // UI 테스트 매니페스트 디버깅
    implementation(libs.androidx.runtime) // Compose Runtime

    androidTestImplementation(libs.androidx.ui.test.junit4) // UI 테스트용 라이브러리
    implementation(libs.kotlinx.coroutines.core)

    // 이미지 추가
    implementation(libs.coil.compose)



}