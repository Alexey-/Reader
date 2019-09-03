package com.example.reader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.base.model.base.NetworkResource
import com.example.base.model.database.NetworkResourceStateDao
import com.example.base.model.database.RoomDatabaseWrapper
import com.example.base.model.database.convertors.*
import com.example.reader.model.article.storage.Article
import com.example.reader.model.article.storage.ArticleContent
import com.example.reader.model.article.storage.ArticleDao

@Database(entities = [
    Article::class,
    ArticleContent::class,
    NetworkResource.State::class
], version = 1)
@TypeConverters(
    FileConvertor::class,
    LocalDateConvertor::class,
    LocalDateTimeConvertor::class,
    StringListConvertor::class,
    StringMapConvertor::class
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun networkResourceStateDao(): NetworkResourceStateDao

    companion object {
        fun createDatabase(context: Context): MainDatabase {
            return Room
                .databaseBuilder(context, MainDatabase::class.java, "main.db")
                .build()
        }
    }

    inner class Wrapper : RoomDatabaseWrapper(this@MainDatabase)
    val wrapper = Wrapper()

}