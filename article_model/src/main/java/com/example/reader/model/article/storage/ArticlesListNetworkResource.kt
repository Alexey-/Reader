package com.example.reader.model.article.storage

import com.example.base.model.base.NetworkResource
import com.example.base.model.database.IDatabase
import com.example.reader.model.article.api.ArticleApi
import com.example.reader.model.article.api.ArticleDto
import io.reactivex.Single
import org.joda.time.Period

class ArticlesListNetworkResource(
    private val database: IDatabase,
    private val api: ArticleApi
) : NetworkResource<List<ArticleDto>, List<Article>>(database) {

    private val dao: ArticleDao = database.getDao(ArticleDao::class.java)
    override val resourceId: String = "singleton"
    override val updatePeriod: Period = Period.minutes(15)

    override fun loadData(): List<Article>? {
        return dao.loadArticles()
    }

    override fun fetchData(): Single<List<ArticleDto>> {
        return api.queryArticles(null, 20).map { it.items }
    }

    override fun saveData(response: List<ArticleDto>) {
        val newArticles = response.map { Article(it) }
        database.runInTransaction {
            dao.deleteArticles()
            dao.saveArticles(newArticles)
        }
    }

}