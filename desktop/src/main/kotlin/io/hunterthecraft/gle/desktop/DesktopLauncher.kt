package io.hunterthecraft.gle.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.hunterthecraft.gle.core.RoadMVPGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("OpenTTD Road MVP - LibGDX Kotlin")
    config.setWindowedMode(1024, 768)
    config.useVsync(true)

    Lwjgl3Application(RoadMVPGame(), config)
}
