package com.example.aplikasistory.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasistory.R
import com.example.aplikasistory.data.model.response.ListStoryItem
import com.example.aplikasistory.databinding.ActivityMainBinding
import com.example.aplikasistory.view.ViewModelFactory
import com.example.aplikasistory.view.addstory.AddStoryActivity
import com.example.aplikasistory.view.addstory.AddStoryViewModel
import com.example.aplikasistory.view.splashscreen.SplashScreen
import com.example.aplikasistory.view.story.DetailStoryActivity
import com.example.aplikasistory.view.story.StoryAdapter
import com.example.aplikasistory.view.story.StoryViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var addstoryViewModel: AddStoryViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[StoryViewModel::class.java]

        addstoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[AddStoryViewModel::class.java]

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                token = user.token
                storyViewModel.getStory(token)
            } else {
                startActivity(Intent(this, SplashScreen::class.java))
                finish()
            }
        }

        adapter = StoryAdapter()
        storyViewModel.listStoryUser.observe(this) { listUser ->
            listUser?.let {
                binding.rvListStory.adapter = adapter
                showLoading(false)
                adapter.setListUser(it)
            }
        }

        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(user: ListStoryItem) {
                showLoading(false)
                Intent(this@MainActivity, DetailStoryActivity::class.java).also {
                    it.putExtra(DetailStoryActivity.EXTRA_LIST_STORY_ITEM, user)
                    startActivity(it)
                }
            }
        })

        binding.apply {
            rvListStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = adapter
        }


        binding.toggleAdd.setOnClickListener {
            val addStoryIntent = Intent(this@MainActivity, AddStoryActivity::class.java)
            addStoryIntent.putExtra("TOKEN_KEY", token)
            startActivity(addStoryIntent)
        }

        addstoryViewModel.navigateToMain.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutButton -> {
                viewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}