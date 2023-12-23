plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinPluginParcelize)
    alias(libs.plugins.jetbrainsKotlinPluginSerialization)
    alias(libs.plugins.androidxNavigationSafeargs)
    alias(libs.plugins.googleDevtoolsKsp)
    alias(libs.plugins.googleDaggerHilt)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.example.ting"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    signingConfigs {
        create("release") {
            storeFile = file("fan")
            storePassword = "1138612367"
            keyAlias = "fan"
            keyPassword = "1138612367"
        }
    }

    defaultConfig {
        applicationId = "com.example.ting"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs["release"]
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-Xcontext-receivers",
            "-opt-in=androidx.paging.ExperimentalPagingApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar", "*.aar")))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    // Lifecycle
    implementation(libs.androidx.lifecycle)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)

    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // Startup
    implementation(libs.androidx.startup)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)

    // Serialization
    implementation(libs.kotlinx.serialization)
    implementation(libs.converter.json)

    // Network
    implementation(libs.okhttp)
    implementation(libs.retrofit)

    // Coil
    implementation(libs.coil)

    // Krypto
    implementation(libs.krypto)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    // Coroutines
    implementation(libs.kotlinx.coroutines.guava)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}