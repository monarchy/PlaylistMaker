plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
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
}
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}


dependencies {
    implementation(libs.androidx.ui.test.android)
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Choose one of the following:
    // Material Design 3
    implementation(libs.androidx.compose.material3.material32)
    implementation(libs.androidx.compose.ui.ui2)

    // Android Studio Preview support
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.adaptive)

    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose.v192)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose.v285)
    // Optional - Integration with LiveData
    implementation(libs.runtime.livedata)
    // Optional - Integration with RxJava
    implementation(libs.runtime.rxjava2)
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation (libs.glide)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.storage)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.ui.tooling.preview.android)
    annotationProcessor (libs.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

}