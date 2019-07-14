package com.example.reader.model.article.api

import com.example.base.model.network.responses.ListResponse
import com.example.base.model.network.responses.ObjectResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

public interface ArticleApi {

    @GET("v1/article/list")
    fun queryArticles(@Query("sinceId") sinceId: Long?, @Query("limit") limit: Int): Single<ListResponse<ArticleDto>>

    @GET("v1/article/{id}/content")
    fun queryArticleContent(@Query("articleId") articleId: Long): Single<ObjectResponse<ArticleContentDto>>

}