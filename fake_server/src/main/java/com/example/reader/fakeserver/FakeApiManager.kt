package com.example.reader.fakeserver

import com.example.base.model.network.IApiManager
import com.example.base.model.user.ISession
import com.example.reader.model.article.api.ArticleApi
import com.google.gson.GsonBuilder

class FakeApiManager : IApiManager {

    override fun <T> createApi(apiClass: Class<T>, userSession: ISession?, gsonBuilder: GsonBuilder): T {
        return when (apiClass) {
            ArticleApi::class.java -> FakeArticleApi() as T
            else -> throw error("${apiClass.simpleName} has no fake implementation")
        }
    }

}