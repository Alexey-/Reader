package com.example.base.model.database

interface IDatabase {

    fun runInTransaction(runnable: () -> Unit)

    fun clearAllTables()

    fun <T> getDao(daoClass: Class<T>): T

}
