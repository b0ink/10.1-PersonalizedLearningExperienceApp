import java.util.Properties


plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.personalizedlearningexperience"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.personalizedlearningexperience"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-wallet:19.3.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.auth0.android:jwtdecode:2.0.2")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}