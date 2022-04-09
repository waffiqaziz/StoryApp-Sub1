package com.dicoding.storyapp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.helper.showToast
import com.dicoding.storyapp.ui.adapter.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.ListStoryViewModel
import com.google.android.material.snackbar.Snackbar

class ListStoryActivity : AppCompatActivity() {

  private lateinit var binding: ActivityListStoryBinding
  private lateinit var user: UserModel

  private val viewModel by viewModels<ListStoryViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityListStoryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    user = intent.getParcelableExtra(EXTRA_USER)!!

    viewModel.itemStory.observe(this) {
      setListStory(it)
    }

    viewModel.showListStory(user.token)

    viewModel.isLoading.observe(this) {
      showLoading(it)
    }
    showSnackBar()
  }

  private fun setListStory(itemStory: List<ListStoryItem>) {
    val listStory = ArrayList<ListStoryItem>()
    for (story in itemStory) {
      val dataStory = ListStoryItem(
        story.photoUrl,
        story.createdAt,
        story.name,
        story.description,
        story.lon,
        story.id,
        story.lat,
      )
      listStory.add(dataStory)
    }

    val adapter =
      StoryAdapter(listStory, object : StoryAdapter.OnItemClickCallback {
        override fun onItemClicked(data: ListStoryItem) {
//          val moveUserDetail = Intent(this@ListStoryActivity, DetailUserActivity::class.java)
//          moveUserDetail.putExtra(DetailUserActivity.EXTRA_USER, data)
//
//          // goto DetailActivity with an animation(explode)
//          startActivity(moveUserDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity,).toBundle())

          showToast(this@ListStoryActivity,"belum bisa")
        }
      })
    binding.rvStory.adapter = adapter
  }

  private fun showSnackBar(){
    viewModel.snackBarText.observe(this) {
      it.getContentIfNotHandled()?.let { snackBarText ->
        Snackbar.make(
          findViewById(R.id.rv_story),
          snackBarText,
          Snackbar.LENGTH_SHORT
        ).show()
      }
    }
  }

  private fun showLoading(isLoading: Boolean) {
    binding.apply {
      if (isLoading) {
        progressBar.visibility = View.VISIBLE
      } else {
        progressBar.visibility = View.GONE
      }
    }
  }

  companion object {
    const val EXTRA_USER = "user"
  }
}