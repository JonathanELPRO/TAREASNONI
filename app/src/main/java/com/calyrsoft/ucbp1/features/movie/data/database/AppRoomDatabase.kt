package com.calyrsoft.ucbp1.features.movie.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.calyrsoft.ucbp1.features.dollar.data.database.dao.IDollarDao
import com.calyrsoft.ucbp1.features.dollar.data.database.entity.DollarEntity
import com.calyrsoft.ucbp1.features.movie.data.database.dao.IMovieDao
import com.calyrsoft.ucbp1.features.movie.data.database.entity.MovieEntity


val MIGRATION_6_7 = object : Migration(startVersion = 6, endVersion = 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `movies` ADD COLUMN `timestamp` INTEGER NOT NULL DEFAULT 0")
        database.execSQL("UPDATE movies SET timestamp = strftime('%s','now') * 1000 WHERE timestamp = 0")
    }
}

@Database(entities = [MovieEntity::class], version = 7, exportSchema = true)
abstract class AppRoomDatabaseMovies : RoomDatabase() {
    abstract fun movieDao(): IMovieDao



    companion object {
        @Volatile
        private var Instance: AppRoomDatabaseMovies? = null




        fun getDatabase(context: Context): AppRoomDatabaseMovies {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabaseMovies::class.java,
                    "movie_db"
                )
                    //.fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_6_7)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
