package com.dicoding.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.showToast
import com.dicoding.storyapp.ui.activity.AddStoryActivity
import com.dicoding.storyapp.ui.activity.ListStoryActivity
import com.dicoding.storyapp.ui.activity.SignInActivity
import com.dicoding.storyapp.ui.viewmodel.MainViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : AppCompatActivity() {
  private lateinit var user: UserModel
  private lateinit var mainViewModel: MainViewModel
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupViewModel()
    setupAction()
    playAnimation()
    buttonListener()
  }

  private fun setupViewModel() {
    mainViewModel = ViewModelProvider(
      this,
      ViewModelFactory(UserPreference.getInstance(dataStore))
    )[MainViewModel::class.java]

    mainViewModel.getUser().observe(this) {
      user = UserModel(
        it.name,
        it.email,
        it.password,
        it.userId,
        it.token,
        true
      )
      binding.nameTextView.text = getString(R.string.greeting, it.name)
      showToast(this, user.token)
    }
  }

  private fun setupAction() {
    binding.btnLogOut.setOnClickListener {
      mainViewModel.logout()
      AlertDialog.Builder(this).apply {
        setTitle(getString(R.string.information))
        setMessage(getString(R.string.log_out_success))
        setPositiveButton(getString(R.string.continue_)) { _, _ ->
          startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }
        create()
        show()
      }
    }
  }

  private fun playAnimation() {
    ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
      duration = 6000
      repeatCount = ObjectAnimator.INFINITE
      repeatMode = ObjectAnimator.REVERSE
    }.start()

    val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
    val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
    val logout = ObjectAnimator.ofFloat(binding.btnLogOut, View.ALPHA, 1f).setDuration(500)

    AnimatorSet().apply {
      playSequentially(name, message, logout)
      startDelay = 500
    }.start()
  }

  private fun buttonListener() {
    binding.btnAddStory.setOnClickListener {
      startActivity(Intent(this, AddStoryActivity::class.java))
    }

    binding.btnLisStory.setOnClickListener {
      val moveToListStoryActivity = Intent(this@MainActivity, ListStoryActivity::class.java)
      moveToListStoryActivity.putExtra(ListStoryActivity.EXTRA_USER, user)
      startActivity(moveToListStoryActivity)
    }
  }
}