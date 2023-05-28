package com.example.picodiploma.storyapp.view.paginglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.picodiploma.storyapp.adapter.PagingCardAdapter
import com.example.picodiploma.storyapp.data.ApiServiceHelper
import com.example.picodiploma.storyapp.data.PagingListRepository
import com.example.picodiploma.storyapp.databinding.ActivityPagingListBinding
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
        viewModel = ViewModelProvider(this, ViewModelFactory(repository)).get(PagingListViewModel::class.java)

        adapter = PagingCardAdapter()

        binding.recyclerViewList.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewList.adapter = adapter

        lifecycleScope.launch {
            viewModel.getStories().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}