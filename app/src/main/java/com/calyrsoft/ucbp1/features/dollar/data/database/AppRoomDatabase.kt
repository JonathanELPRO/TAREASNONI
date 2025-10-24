package com.calyrsoft.ucbp1.features.dollar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.calyrsoft.ucbp1.features.dollar.data.database.dao.IDollarDao
import com.calyrsoft.ucbp1.features.dollar.data.database.entity.DollarEntity

//la clase de abajo es abstracta porque tiene metodos abstractos pero tranquilamente podria no tenerlos
@Database(entities = [DollarEntity::class], version = 10)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun dollarDao(): IDollarDao
    //Es una función abstracta es decir tu no te encargadas de su implementacion
    // room lo hara por ti, esta funcion que devuelve un objeto que implementa la interfaz IDollarDao.
    //con ese objeto ya podras usar los metodos qu edefiniste en la interfaz
    //es decir room ya desarollo los metodos de la interfaz por ti
    //nota que retorna un objeto o instancia que implementa una interfaz esto es diferente
    //a una clase que implementa una interfaz a partir de la cual puedo crear varias instancias o objetos
    //con este objeto que implementa la interfaz solo tengo una sola instancia es como un singleton
    //en realidad lo que sucede por detras es lo siguiente room implememnta la interfaz con una clase
    //y al llamar a este metodo abstracto room te devuelbve un objeto o una inatancia de esa clase



    //compaanion object es como un metodo estatico, dentro de el debemos definir un metodo estatico
    companion object {
        //Volatile asegura que todos los hilos vean la misma variable Instance., porque nosotros trabajamos con hilos
        @Volatile
        private var Instance: AppRoomDatabase? = null
        //el metodo estatico que puede ser accedido desde cualquier lugar de esta clase
        //nos devolvera una unica instancia de esta clase mas adelante, arriba esta la unica instancia que devolvera




        fun getDatabase(context: Context): AppRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "dollar_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

//return Instance ?: synchronized(this)
// Si Instance ya existe → la devuelve.
//
//Si no existe (null) → entra en el bloque synchronized.
//
//synchronized(this): asegura que solo un hilo pueda crear la base de datos al mismo tiempo (thread-safe).

//Room.databaseBuilder(...)
//
//Lo de arriba realmente construye  la base de datos y crea una instancia con ayuda de build:
//
//context: acceso a Android para guardar el archivo.
//
//AppRoomDatabase::class.java: tu clase abstracta que define la BD.
//
//"dollar_db": el nombre del archivo SQLite.
//
//.build() → crea la instancia.
//en el also el objeto recien creado lo estamos poniendo en la variable instance a la cual todos podemos acceder gracias a nuestro metodo estatico