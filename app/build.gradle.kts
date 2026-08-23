plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.techfix.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.techfix.app"
        minSdk = 24
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
}

dependencies {

    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // GPS / Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // PayHere Android SDK
    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.18")

    // Gson - required by PayHere SDK
    implementation("com.google.code.gson:gson:2.8.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}