package com.example.reader.ui.article

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.base.model.base.INetworkResource
import com.example.base.ui.base.NetworkResourceFragment
import com.example.base.ui.view.ErrorView
import com.example.reader.model.article.IArticleManager
import com.example.reader.model.article.storage.Article
import com.example.reader.ui.article.databinding.FragmentArticlesListBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ArticlesListFragment : NetworkResourceFragment<List<Article>, FragmentArticlesListBinding>() {

    @set:Inject
    internal lateinit var articleManager: IArticleManager

    override lateinit var networkResource: INetworkResource<List<Article>>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        networkResource = articleManager.getArticlesList()
    }

    override val layoutId = R.layout.fragment_articles_list
    override val swipeRefreshLayout: SwipeRefreshLayout? = binding.swipeLayout
    override val dataView: View = binding.data
    override val emptyDataView: View? = null
    override val errorView: ErrorView? = null
    private var adapter: ArticlesListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.data.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = ArticlesListAdapter()
        binding.data.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override fun isDataEmpty(data: List<Article>): Boolean {
        return data.isEmpty()
    }

    override fun onDataChanged(data: List<Article>) {
        adapter!!.articles = data
    }

}