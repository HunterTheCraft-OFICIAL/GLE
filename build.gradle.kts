plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("application")
}

group = "io.hunterthecraft.gle"
version = "0.1.0"

repositories {
    mavenCentral()
}

val libgdxVersion = "1.12.1"
val kotlinVersion = "1.9.20"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:$libgdxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    
    // Platform-specific backend (desktop)
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-desktop")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.hunterthecraft.gle.desktop.DesktopLauncher")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.hunterthecraft.gle.desktop.DesktopLauncher"
    }
    
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
