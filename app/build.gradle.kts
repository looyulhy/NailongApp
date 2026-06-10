plugins {
    id("com.android.application") version "8.7.3" apply true
    id("org.jetbrains.kotlin.android") version "2.1.0" apply true
}

android {
    namespace = "com.nailong.laugh"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nailong.laugh"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    // Zero extra dependencies - pure Android SDK
}
