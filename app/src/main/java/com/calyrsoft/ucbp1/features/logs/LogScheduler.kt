package com.calyrsoft.ucbp1.features.logs

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class LogScheduler(
    private val context: Context
) {
    private val LOG_WORKNAME = "logUploadWork" //le damos un nombre a nuestro work
    private val INTERVAL_MINUTES = 15L


    fun schedulePeriodicaUpload() {
        val logRequest = PeriodicWorkRequest.Builder( //creamos un request de work
            LogUploadWorker::class.java, //esto en si es un CoroutineWorker, es el work como tal
            INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()  //le estamos diciendo que para que el work funcione se necesita conexion a internet


        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork( //llamamos a work manager que Encola un trabajo periódico (que se repite en el tiempo) con un nombre único dentro del sistema de WorkManager.
            LOG_WORKNAME,
            //usamos el nombre que le dimos a la solicitud del work como tal
            ExistingPeriodicWorkPolicy.KEEP,
            //si ya hay una solicitud con ese nombre que se mantenga esa
            logRequest
            //enviamos la soliciytd
        )

    }
}
