import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

// local.properties에서 선언한 값들을 불러와 주기 위함
val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        properties.load(stream)
    }
}

android {
    namespace = "com.hp028.portpilot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hp028.portpilot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "NAVER_CLIENT_ID", properties.getProperty("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", properties.getProperty("NAVER_CLIENT_SECRET"))
        buildConfigField("String", "FACEBOOK_APP_ID", properties.getProperty("FACEBOOK_APP_ID"))
        buildConfigField("String", "KAKAO_APP_ID", properties.getProperty("KAKAO_APP_ID"))
        buildConfigField("String", "KAKAO_CLIENT_SCHEME", properties.getProperty("KAKAO_CLIENT_SCHEME"))
        buildConfigField("String", "NAVER_CLIENT_NAME", properties.getProperty("NAVER_CLIENT_NAME"))
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
        buildConfig = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //소셜로그인
    implementation("com.kakao.sdk:v2-all:2.11.2") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.navercorp.nid:oauth:5.9.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha04")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.airbnb.android:lottie:3.1.0")
}