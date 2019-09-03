package com.example.reader.fakeserver

import com.example.base.model.network.responses.ListResponse
import com.example.base.model.network.responses.ObjectResponse
import com.example.reader.model.article.api.ArticleApi
import com.example.reader.model.article.api.ArticleContentDto
import com.example.reader.model.article.api.ArticleDto
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.math.min

class FakeArticleApi : ArticleApi {

    private val articles: List<ArticleDto> = ArrayList<ArticleDto>().apply {
        for (i in 0..90) {
            add(ArticleDto(id = i.toLong(), title = "Fake article $i", preview = "Short text preview for article $i"))
        }
    }

    override fun queryArticles(sinceId: Long?, limit: Int): Single<ListResponse<ArticleDto>> {
        if (sinceId == null) {
            return Single.timer(1, TimeUnit.SECONDS).map {
                ListResponse(articles.subList(0, limit))
            }
        } else {
            return Single.timer(1, TimeUnit.SECONDS).map {
                val startIndex = articles.indexOfFirst { it.id == sinceId } + 1
                if (startIndex == articles.size) {
                    ListResponse(emptyList())
                } else {
                    val endIndex = min(startIndex + limit, articles.size)
                    ListResponse(articles.subList(startIndex, endIndex))
                }
            }
        }
    }

    override fun queryArticleContent(articleId: Long): Single<ObjectResponse<ArticleContentDto>> {
        return Single.timer(1, TimeUnit.SECONDS).map {
            ObjectResponse(
                ArticleContentDto(articleId, "This is content of article $articleId. " +
                    "It can can contain <b>random</b> <i>html content</i>. ")
            )
        }
    }

}