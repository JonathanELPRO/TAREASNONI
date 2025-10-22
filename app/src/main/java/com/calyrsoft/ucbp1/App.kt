package com.calyrsoft.ucbp1

import android.app.Application
import com.calyrsoft.ucbp1.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application(){
    override fun onCreate(){
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(appModule)
        }
    }


}

//Todo lo que pongas dentro de onCreate() se ejecuta antes de que cualquier Activity o Fragment aparezca en pantalla.

//el override indica que se sobreescriba el metodo onCreate en la sobreescritura llamamos al metodo del
// padre(Apliccation) para que este onCreate haga algunas cosas que su padre ya hacia

//crearmos este archivo llamado App pues es con lo que inicializara mi aplicacion, si te das cuenta
//aqui esta la configuracion de Koin pues es lo primero que debe suceder antes de ver cualquier pantalla, luego
//recien se realizan otras actividades en este caso solo tenemos una que se llama MainActivity que se encarga
//de mostrar el GithubScreen
//prueba push