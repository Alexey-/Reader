package com.example.reader.model.article.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reader.model.article.api.ArticleDto

@Entity
data class Article(
    @PrimaryKey val id: Long,
    val title: String,
    val preview: String
) {

    constructor(dto: ArticleDto) : this(
        dto.id!!,
        dto.title!!,
        dto.preview!!
    )

}