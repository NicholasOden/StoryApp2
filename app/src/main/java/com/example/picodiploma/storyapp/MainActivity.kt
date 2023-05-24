package com.example.picodiploma.storyapp

import com.example.picodiploma.storyapp.paginglist.PagingListActivity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.picodiploma.storyapp.adapter.StoryAdapter
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.picodiploma.storyapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var apiServiceHelper: ApiServiceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this)
        binding.rVMain.layoutManager = layoutManager
        adapter = StoryAdapter()
        binding.rVMain.adapter = adapter

        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        apiServiceHelper = ApiServiceHelper(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storyList = apiServiceHelper.getStoryList()
                Log.d("MainActivity", "Story list: $storyList")
                withContext(Dispatchers.Main) {
                    adapter.submitList(storyList)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching stories: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch stories: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.fabCreateStory.setOnClickListener {
            // Open the create story activity
            val intent = Intent(this, CreateStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logout()
                true
            }

            R.id.menu_map -> {
                // Create an intent to start the SettingsActivity.
                val intent = Intent(this, MapsActivity::class.java)
                // Start the activity.
                startActivity(intent)
                return true
            }

            R.id.menu_paging_list -> {
                // Create an intent to start the paginglist.
                val intent = Intent(this, PagingListActivity::class.java)
                // Start the activity.
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Delete user's token and session information from shared preferences
        val sharedPrefs = getSharedPreferences("storyapp", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            remove("token")
            apply()
        }

        // Start the LoginActivity and clear the activity stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}



