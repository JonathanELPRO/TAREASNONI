package com.calyrsoft.ucbp1.features.notification.data.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.calyrsoft.ucbp1.MainActivity
import com.calyrsoft.ucbp1.navigation.Screen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.calyrsoft.ucbp1.R

class FirebaseService : FirebaseMessagingService() {
    companion object {
        val TAG = FirebaseService::class.java.simpleName
    }
//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
//    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            if ( /* Check if data needs to be processed by long running job */true) { // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob()
            } else { // Handle message within 10 seconds
                // handleNow()
            }
        }


        // Si viene con notification
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            showNotification(it.title ?: "Notificación", it.body ?: "")
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "fcm_default_channel"
        //crearemos un canal mas adelante
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //NotificationManager → es el sistema de Android que se encarga de mostrar notificaciones.

        // 👇 Intent a MainActivity con ruta "github_screen"
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateTo", "github") // ejemplo: "github_screen"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        //Crea un Intent que abrirá MainActivity.

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        //el intent que abre main activity con otro screen lo volvemos como pending
        //es decir es un inteneto en pausa, en pausa porque no se ejecutara hasta que alguien presiona la notificacion

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Notificaciones FCM",
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }
        }
        //arriba estamos creando el canal de la notificacion

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification) // 👈 asegúrate que exista
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // 👈 al tocar la notificación abre GithubScreen
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        //arriba decimos como sera la notificacion pero aun no la hemos emitido

        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        //aqui ya definimos la notificacion, lo primero que le mandamos es el id
    }



}

