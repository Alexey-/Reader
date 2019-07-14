package com.example.reader.model.article

import com.example.reader.model.article.storage.ArticleContentNetworkResource
import com.example.reader.model.article.storage.ArticlesListNetworkResource

interface IArticleManager {

    fun getArticlesList(): ArticlesListNetworkResource

    fun getArticleContent(articleId: Long): ArticleContentNetworkResource

}