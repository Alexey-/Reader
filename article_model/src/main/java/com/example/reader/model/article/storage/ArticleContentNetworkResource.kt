package com.example.reader.model.article.storage

import com.example.base.model.base.NetworkResource
import com.example.base.model.database.IDatabase
import com.example.reader.model.article.api.ArticleApi
import com.example.reader.model.article.api.ArticleContentDto
import io.reactivex.Single
import org.joda.time.Period

class ArticleContentNetworkResource(
    private val articleId: Long,
    private val database: IDatabase,
    private val api: ArticleApi
) : NetworkResource<ArticleContentDto, ArticleWithContent>(database) {

    private val dao: ArticleDao = database.getDao(ArticleDao::class.java)
    override val resourceId: String = "article#$articleId"
    override val updatePeriod: Period = Period.minutes(15)

    override fun loadData(): ArticleWithContent? {
        return dao.loadArticleContent(articleId)
    }

    override fun fetchData(): Single<ArticleContentDto> {
        return api.queryArticleContent(articleId).map { it.data }
    }

    override fun saveData(response: ArticleContentDto) {
        val newContent = ArticleContent(response)
        dao.saveArticleContent(newContent)
    }

}