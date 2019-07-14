package com.example.reader.model.article.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reader.model.article.api.ArticleContentDto

@Entity
data class ArticleContent(
    @PrimaryKey val articleId: Long,
    val text: String
) {

    constructor(dto: ArticleContentDto) : this(
        dto.articleId!!,
        dto.text!!
    )

}