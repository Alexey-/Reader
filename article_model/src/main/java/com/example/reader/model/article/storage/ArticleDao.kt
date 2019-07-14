package com.example.reader.model.article.storage

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface ArticleDao {

    @Query("SELECT * FROM Article")
    fun loadArticles(): List<Article>

    @Query("SELECT * FROM Article WHERE id = :id")
    fun loadArticle(id: Long): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveArticles(articles: List<Article>)

    @Query("DELETE FROM Article")
    fun deleteArticles()

    @Delete
    fun deleteArticles(articles: List<Article>)

    @Query("SELECT * FROM Article WHERE id = :articleId")
    fun loadArticleContent(articleId: Long): ArticleWithContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveArticleContent(articleContent: ArticleContent)

    @Query("DELETE FROM ArticleContent WHERE articleId = :articleId")
    fun deleteArticles(articleId: Long)

}