package com.example.picodiploma.storyapp.view.paginglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.picodiploma.storyapp.adapter.PagingCardAdapter
import com.example.picodiploma.storyapp.data.ApiServiceHelper
import com.example.picodiploma.storyapp.data.PagingListRepository
import com.example.picodiploma.storyapp.databinding.ActivityPagingListBinding
import com.example.picodiploma.storyapp.view.CreateStoryActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagingListBinding
    private lateinit var viewModel: PagingListViewModel
    private lateinit var adapter: PagingCardAdapter
    private lateinit var apiServiceHelper: ApiServiceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiServiceHelper = ApiServiceHelper(getToken())

        val repository = PagingListRepository(apiServiceHelper)
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[PagingListViewModel::class.java]

        adapter = PagingCardAdapter()

        binding.recyclerViewList.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewList.adapter = adapter

        lifecycleScope.launch {
            viewModel.getStories().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // Set an OnClickListener for the FAB
        binding.fabCreateStory.setOnClickListener {
            // Open the create story activity
            val intent = Intent(this, CreateStoryActivity::class.java)
            startActivity(intent)
        }

        adapter.addLoadStateListener { loadState ->
            // Show loading spinner during initial load or refresh.
            binding.progressBar.visibility =
                if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            // Show error state if the load fails.
            if (loadState.refresh is LoadState.Error) {
                Toast.makeText(this, "\uD83D\uDE28 Wooops ${(loadState.refresh as LoadState.Error).error}", Toast.LENGTH_LONG).show()
            }

            val isEmptyList = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            // Show empty content message when list is empty.
            if (isEmptyList) {
                Toast.makeText(this, "List is empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}
