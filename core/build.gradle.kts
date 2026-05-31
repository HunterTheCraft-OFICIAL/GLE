plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
}

group = project.property("group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    // LibGDX Core - ALL official dependencies (like pato)
    api("com.badlogicgames.gdx:gdx:$libgdxVersion")
    
    // LibGDX Extensions and Tools
    implementation("com.badlogicgames.gdx:gdx-box2d:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-ai:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-tools:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$libgdxVersion")
    
    // Natives for all platforms
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-android")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-ios")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-html5")
    
    // Additional LibGDX utilities
    implementation("com.badlogicgames.gdx:gdx-bullet:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-net:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-controllers:$libgdxVersion")
    
    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaSourceCompatibility")}")
    targetCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaTargetCompatibility")}")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
