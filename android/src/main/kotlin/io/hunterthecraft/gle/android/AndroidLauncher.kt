package io.hunterthecraft.gle.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.hunterthecraft.gle.core.RoadMVPGame

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = AndroidApplicationConfiguration().apply {
            // Configure Android-specific settings
            useImmersiveMode = true
            hideStatusBar = true
            numSamples = 0 // No anti-aliasing for better performance
            depth = 16
            stencil = 8
        }
        
        initialize(RoadMVPGame(), config)
    }
}
