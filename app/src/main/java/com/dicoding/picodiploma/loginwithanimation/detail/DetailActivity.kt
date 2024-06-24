package com.dicoding.picodiploma.loginwithanimation.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyItem = intent.getParcelableExtra<ListStoryItem>("STORY_ITEM")
        storyItem?.let {
            bindData(it)
        }
    }

    private fun bindData(story: ListStoryItem) {
        binding.tvName.text = story.name
        binding.tvDesc.text = story.description
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetailStory)
    }
}
