package com.calyrsoft.ucbp1.features.movie.data.database.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "movies",
    indices = [Index(value = ["id"], unique = true)])
data class MovieEntity(
    @PrimaryKey()
    @ColumnInfo(name = "id")
    var id: Long = 0,


    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "imageUrl")
    var imageUrl: String? = null,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean,
//
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis(),
)
