package com.example.base.model.network

import com.example.base.model.user.ISession
import com.google.gson.GsonBuilder

interface IApiManager {

    fun <T> createApi(apiClass: Class<T>, userSession: ISession? = null, gsonBuilder: GsonBuilder = GsonBuilder()): T

}