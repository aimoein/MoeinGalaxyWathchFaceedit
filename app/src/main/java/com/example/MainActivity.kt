package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.screens.WatchOptimizedScreen
import com.example.ui.screens.WatchStudioScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.GalaxyWearableHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only enable edge-to-edge on phone devices;
        // Galaxy Watch / Wear OS devices manage circular insets natively.
        val isWatch = GalaxyWearableHelper.isWatchDevice(this)
        if (!isWatch) {
            try {
                enableEdgeToEdge()
            } catch (_: Throwable) {}
        }

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black
                    ) {
                        if (isWatch) {
                            WatchOptimizedScreen()
                        } else {
                            WatchStudioScreen()
                        }
                    }
                }
            }
        }
    }
}
