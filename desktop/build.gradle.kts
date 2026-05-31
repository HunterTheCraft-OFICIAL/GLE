plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("application")
}

group = project.property("group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    // Project core module
    implementation(project(":core"))
    
    // LibGDX Desktop Backend
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$libgdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$libgdxVersion:natives-desktop")
    
    // LWJGL3 specific dependencies
    implementation("org.lwjgl:lwjgl:3.3.1")
    implementation("org.lwjgl:lwjgl-glfw:3.3.1")
    implementation("org.lwjgl:lwjgl-opengl:3.3.1")
}

java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaSourceCompatibility")}")
    targetCompatibility = JavaVersion.valueOf("VERSION_${project.property("javaTargetCompatibility")}")
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
