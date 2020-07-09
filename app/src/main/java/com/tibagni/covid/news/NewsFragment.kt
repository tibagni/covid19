package com.tibagni.covid.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tibagni.covid.R
import com.tibagni.covid.repository.LoadingStatus
import com.tibagni.covid.utils.formatDate
import com.tibagni.covid.utils.or
import com.tibagni.covid.view.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.countries_fragment.view.*
import kotlinx.android.synthetic.main.news_fragment.view.*
import kotlinx.android.synthetic.main.news_fragment.view.emptyView
import kotlinx.android.synthetic.main.news_item.view.*

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val viewModel: NewsViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val newsAdapter = NewsAdapter()

        view.news_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
        viewModel.articles.observe(viewLifecycleOwner) {
            it?.let {
                newsAdapter.refreshData(it)
            }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            view.emptyView.emptyState = when (it.status) {
                LoadingStatus.Status.LOADING -> EmptyView.STATE_LOADING
                LoadingStatus.Status.ERROR -> EmptyView.STATE_ERROR
                LoadingStatus.Status.SUCCESS -> EmptyView.STATE_NO_DATA
            }

            if (it.status == LoadingStatus.Status.ERROR) {
                Toast.makeText(context, getString(R.string.load_fail), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.refresh(false)
    }

    private inner class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        val articles = arrayListOf<Article>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.news_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = articles.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(articles[position])
        }

        fun refreshData(newArticles: List<Article>) {
            articles.clear()
            articles.addAll(newArticles)
            notifyDataSetChanged()
        }

        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val coverImage: ImageView = view.image
            val title: TextView = view.title
            val description: TextView = view.description
            val pubDate: TextView = view.publish_date
            val source: TextView = view.source

            init {
                view.card.setOnClickListener {
                    val article = articles[adapterPosition]
                    val intent = CustomTabsIntent.Builder()
                        .setStartAnimations(
                            requireContext(),
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .setExitAnimations(
                            requireContext(),
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        .build()
                    intent.launchUrl(requireContext(), Uri.parse(article.url))
                }
            }

            fun bind(article: Article) {
                val placeholder = "https://fakeimg.pl/640x300/?text=${article.source}&font=bebas"
                Glide
                    .with(requireContext())
                    .load(article.image.or(placeholder))
                    .placeholder(R.drawable.placeholder)
                    .into(coverImage)
                title.text = article.title
                description.text = article.description
                pubDate.text = article.publishDate.formatDate()
                source.text = article.source
            }
        }
    }
}