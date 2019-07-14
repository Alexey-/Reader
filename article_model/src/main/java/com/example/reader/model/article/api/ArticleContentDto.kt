package com.example.reader.model.article.api

import com.google.gson.annotations.SerializedName

data class ArticleContentDto(
    @SerializedName("id") val articleId: Long?,
    @SerializedName("text") val text: String?
)