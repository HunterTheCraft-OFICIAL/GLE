plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.20"
}

group = project.property("group") as String
version = project.property("version") as String

android {
    namespace = "io.hunterthecraft.gle.android"
    compileSdk = project.property("androidSdkVersion").toString().toInt()

    defaultConfig {
        applicationId = "io.hunterthecraft.gle"
        minSdk = 21
        targetSdk = project.property("androidSdkVersion").toString().toInt()
        versionCode = 1
        versionName = project.property("version") as String
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaSourceCompatibility")}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaTargetCompatibility")}")
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Project core module
    implementation(project(":core"))
    
    // LibGDX Android Backend
    implementation("com.badlogicgames.gdx:gdx-backend-android:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-android")
    
    // Android support libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
