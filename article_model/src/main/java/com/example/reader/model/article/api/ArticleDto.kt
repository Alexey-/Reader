package com.example.reader.model.article.api

import com.google.gson.annotations.SerializedName

data class ArticleDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("preview") val preview: String?
)