package com.example.base.model.database

import androidx.room.RoomDatabase

import java.util.HashMap

open class RoomDatabaseWrapper(private val roomDatabase: RoomDatabase) : IDatabase {

    private val daoByClass: HashMap<Class<*>, Any> = HashMap()

    override fun runInTransaction(runnable: () -> Unit) {
        roomDatabase.runInTransaction(runnable)
    }

    override fun clearAllTables() {
        roomDatabase.clearAllTables()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getDao(daoClass: Class<T>): T {
        var dao: T? = daoByClass[daoClass] as? T?
        if (dao != null) {
            return dao
        }

        try {
            val databaseClass = roomDatabase.javaClass
            val methods = databaseClass.declaredMethods
            for (i in methods.indices) {
                val method = methods[i]
                if (daoClass.isAssignableFrom(method.returnType)) {
                    dao = method.invoke(roomDatabase) as T
                    daoByClass[daoClass] = dao as Any
                    return dao
                }
            }
            throw Exception("No method with assignable return type")
        } catch (e: Throwable) {
            throw RuntimeException("Cannot find dao ${daoClass.simpleName} in database ${roomDatabase.javaClass.simpleName}", e)
        }
    }

}
