import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
} else {
    throw GradleException("local.properties file not found")
}

// Now get your values safely
val apiKey: String = localProperties.getProperty("API_KEY")
        ?: throw GradleException("API_KEY not found in local.properties")

val accountId: String = localProperties.getProperty("ACCOUNT_ID")
        ?: throw GradleException("ACCOUNT_ID not found in local.properties")

val taskId: String = localProperties.getProperty("TASK_ID")
        ?: throw GradleException("TASK_ID not found in local.properties")

val groupId: String = localProperties.getProperty("GROUP_ID")
        ?: throw GradleException("GROUP_ID not found in local.properties")

android {
    namespace = "com.pawan.aadhaarcard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pawan.aadhaarcard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "ACCOUNT_ID", "\"$accountId\"")
        buildConfigField("String", "TASK_ID", "\"$taskId\"")
        buildConfigField("String", "GROUP_ID", "\"$groupId\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.google.mlkit:text-recognition:16.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.airbnb.android:lottie:6.0.0")
}
