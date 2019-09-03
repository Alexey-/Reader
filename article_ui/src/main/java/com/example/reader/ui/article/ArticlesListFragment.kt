package com.example.reader.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.base.model.base.INetworkResource
import com.example.base.ui.base.NetworkResourceFragment
import com.example.base.ui.view.ErrorView
import com.example.reader.model.article.IArticleManager
import com.example.reader.model.article.storage.Article
import com.example.reader.ui.article.databinding.FragmentArticlesListBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ArticlesListFragment : NetworkResourceFragment<List<Article>>() {

    companion object {

        @JvmStatic
        fun newInstance(): ArticlesListFragment {
            val arguments = Bundle()
            val fragment = ArticlesListFragment()
            fragment.arguments = arguments
            return fragment
        }

    }

    @set:Inject
    internal lateinit var articleManager: IArticleManager

    override lateinit var networkResource: INetworkResource<List<Article>>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        networkResource = articleManager.getArticlesList()
    }

    private var _binding: FragmentArticlesListBinding? = null
    private val binding: FragmentArticlesListBinding
        get() = _binding ?: throw Exception("View not attached")

    private var adapter: ArticlesListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentArticlesListBinding.inflate(inflater, container, false)

        binding.data.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = ArticlesListAdapter()
        binding.data.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = binding.swipeLayout
    override val dataView: View
        get() = binding.data
    override val emptyDataView: View?
        get() = binding.empty
    override val errorView: ErrorView?
        get() = binding.error

    override fun isDataEmpty(data: List<Article>): Boolean {
        return data.isEmpty()
    }

    override fun onDataChanged(data: List<Article>) {
        adapter!!.articles = data
    }

    override val title: String?
        get() = getString(R.string.generic_details)

}