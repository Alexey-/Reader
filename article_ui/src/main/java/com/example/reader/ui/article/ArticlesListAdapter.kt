package com.example.reader.ui.article

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reader.model.article.storage.Article
import com.example.reader.ui.article.databinding.ViewHolderArticleBinding

class ArticlesListAdapter : RecyclerView.Adapter<ArticlesListAdapter.ArticleViewHolder>() {

    var articles: List<Article> = emptyList()
        set(value) {
            val oldArticles = field
            val newArticles = value
            field = value

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldArticles.size
                }

                override fun getNewListSize(): Int {
                    return newArticles.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldArticles[oldItemPosition].id == newArticles[oldItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldArticles[oldItemPosition] == newArticles[oldItemPosition]
                }
            }).dispatchUpdatesTo(this)
        }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_article, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ArticleViewHolder, position: Int) {
        viewHolder.article = articles[position]
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ViewHolderArticleBinding.bind(itemView)

        var article: Article? = null
            set(value) {
                field = value
                value ?: return

                binding.title.text = value.title
                binding.preview.text = value.preview
            }

    }

}