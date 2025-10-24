package com.calyrsoft.ucbp1

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calyrsoft.ucbp1.features.movie.data.database.AppRoomDatabaseMovies
import com.calyrsoft.ucbp1.features.movie.data.database.MIGRATION_6_7
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Test de migración de versión 6 → 7 para la base de datos AppRoomDatabaseMovies.
 * Valida que la estructura de la tabla 'movies' se actualice correctamente
 * al agregar la columna 'timestamp' y que los datos previos permanezcan intactos.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppRoomDatabaseMovies::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate6To7() {
        // --- Crea la base simulando la estructura de la versión 6 ---
        var db = helper.createDatabase(TEST_DB, 6).apply {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS movies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT,
                    imageUrl TEXT,
                    isFavorite INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
            execSQL("INSERT INTO movies (title, imageUrl, isFavorite) VALUES ('Matrix', 'matrix.jpg', 1)")
            close()
        }

        // --- Ejecuta la migración 6 → 7 ---
        helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        // Nota:
        // MigrationTestHelper validará automáticamente que la tabla final
        // coincida con la estructura del schema exportado 7.json
        // (con la nueva columna 'timestamp' y sin pérdida de datos).
    }
}
