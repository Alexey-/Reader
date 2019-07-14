package com.example.reader.model.article.storage

import androidx.room.Embedded
import androidx.room.Relation

data class ArticleWithContent(
    @Embedded
    val article: Article,
    @Relation(parentColumn = "id", entityColumn = "articleId", entity = ArticleContent::class)
    val content: ArticleContent
)