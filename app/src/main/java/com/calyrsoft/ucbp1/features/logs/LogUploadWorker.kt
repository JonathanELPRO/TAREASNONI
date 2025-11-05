package com.calyrsoft.ucbp1.features.logs

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calyrsoft.ucbp1.features.movie.domain.usecase.GetPopularMoviesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LogUploadWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) , KoinComponent {
    //lo de arriba se lee como hereda de tal clase pero implementa tal interfaz(KoinComponent)
    // podria implementar varias otras interfaces, pero esa interfaz me permite usa el by inject,


    private val fetchPopularMoviesUseCase: GetPopularMoviesUseCase by inject()


    override suspend fun doWork(): Result {


        println("ejecutar instrucción para subir datos")
        val response = fetchPopularMoviesUseCase.invoke(1)
        response.fold(
            onFailure = {
                return Result.failure()
            },
            onSuccess = {
                println("datos subidos ${it.size}")
                return Result.success()
            }
        )


    }
}

