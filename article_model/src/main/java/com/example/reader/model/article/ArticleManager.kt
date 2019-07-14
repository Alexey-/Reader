package com.example.reader.model.article

import com.example.base.model.database.IDatabase
import com.example.base.model.network.IApiManager
import com.example.reader.model.article.api.ArticleApi
import com.example.reader.model.article.storage.ArticleContentNetworkResource
import com.example.reader.model.article.storage.ArticlesListNetworkResource
import java.lang.ref.WeakReference

class ArticleManager(
    private val database: IDatabase,
    private val apiManager: IApiManager
) : IArticleManager {

    private val api: ArticleApi = apiManager.createApi(ArticleApi::class.java)
    private val articlesList: ArticlesListNetworkResource = ArticlesListNetworkResource(database, api)
    private val articlesCache: HashMap<Long, WeakReference<ArticleContentNetworkResource>> = HashMap()

    override fun getArticlesList(): ArticlesListNetworkResource {
        return articlesList
    }

    override fun getArticleContent(articleId: Long): ArticleContentNetworkResource {
        var cachedValue = articlesCache[articleId]?.get()
        if (cachedValue == null) {
            cachedValue = ArticleContentNetworkResource(articleId, database, api)
            articlesCache[articleId] = WeakReference(cachedValue)
        }
        return cachedValue
    }

}