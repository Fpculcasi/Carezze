package com.fpculcasi.carezze.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CareNestMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO(M7): route notification to correct deep-link screen
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO(M7): persist FCM token to Firestore users/{uid}.fcmTokens
    }
}
