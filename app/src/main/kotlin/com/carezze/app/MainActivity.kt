package com.fpculcasi.carezze

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fpculcasi.carezze.data.service.EXTRA_PERSON_ID
import com.fpculcasi.carezze.data.service.EXTRA_ROUTE_TYPE
import com.fpculcasi.carezze.data.service.EXTRA_THERAPY_ID
import com.fpculcasi.carezze.ui.navigation.AppNavigation
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent()
        setContent {
            CarezzeTheme {
                AppNavigation()
            }
        }
    }

    private fun handleNotificationIntent() {
        val routeType = intent?.getStringExtra(EXTRA_ROUTE_TYPE) ?: return
        val personId = intent.getStringExtra(EXTRA_PERSON_ID) ?: ""
        val therapyId = intent.getStringExtra(EXTRA_THERAPY_ID) ?: ""
        // TODO(M7-7.4): pass (routeType, personId, therapyId) to AppNavigation for deep-link routing
        Log.d("MainActivity", "Notification tap: type=$routeType person=$personId therapy=$therapyId")
    }
}
