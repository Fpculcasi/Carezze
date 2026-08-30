package com.fpculcasi.carezze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fpculcasi.carezze.ui.navigation.AppNavigation
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarezzeTheme {
                AppNavigation()
            }
        }
    }
}
