package com.dicoding.storyapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import com.dicoding.storyapp.R

class DetailStoryActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail_story)
  }


  companion object {
    const val EXTRA_STORY = "story"
  }
}