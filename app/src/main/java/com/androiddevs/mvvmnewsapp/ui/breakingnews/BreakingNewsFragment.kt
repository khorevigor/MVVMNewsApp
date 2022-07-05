package com.androiddevs.mvvmnewsapp.ui.breakingnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.utils.TAG
import com.androiddevs.mvvmnewsapp.utils.viewbinding.ViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BreakingNewsFragment :
    ViewBindingFragment<FragmentBreakingNewsBinding>(FragmentBreakingNewsBinding::inflate) {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViewAdapter()

        collectLatestLifeCycleFlow(viewModel.breakingNews) { response ->
            when (response) {
                is Resource.Success -> handleSuccessfulResponse(response)
                is Resource.Error -> handleErrorResponse(response)
                is Resource.Loading -> handleLoading()
            }
        }
    }

    private fun handleSuccessfulResponse(response: Resource.Success<NewsResponse>) {
        hideProgressBar()
        response.data?.let {
            newsAdapter.differ.submitList(it.articles)
        }
    }

    private fun handleErrorResponse(response: Resource.Error<*>) {
        hideProgressBar()
        response.message?.let {
            Log.e(TAG, "Error occurred: $it")
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLoading() {
        showProgressBar()
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun <T> collectLatestLifeCycleFlow(flow: Flow<T>, collector: suspend (T) -> Unit) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collector)
            }
        }
    }
}
