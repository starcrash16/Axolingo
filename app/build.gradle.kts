plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.proyecto_final.axolingo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.proyecto_final.axolingo"
        minSdk = 29
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.auth) // Esta usa el catálogo de versiones
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.code.gson:gson:2.13.2")  //biblioteca para convertir texto JSON a lista de objetos
    implementation("com.google.mlkit:text-recognition:16.0.0")  //dependencia para convertir el trazo a texto
    //implementation("com.google.mlkit:digital-ink-recognition:18.1.0")  //dependencia para convertir el trazo a texto
    //implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    val room_version = "2.8.4"  //implementacion de room para la bd
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    //corrutinas de kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.room:room-ktx:2.6.1")  //corrutinas para Room
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")  //corrutinas para ViewModel
    //DataStore (Preferences DataStore)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}