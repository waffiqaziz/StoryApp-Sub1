package com.dicoding.storyapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.storyapp.ui.viewmodel.SignInViewModel
import com.dicoding.storyapp.MainActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.databinding.ActivitySigninBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignInActivity : AppCompatActivity() {
  private lateinit var signInViewModel: SignInViewModel
  private lateinit var binding: ActivitySigninBinding
  private lateinit var user: UserModel

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivitySigninBinding.inflate(layoutInflater)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setupViewModel()
    setMyButtonEnable()
    editTextListener()
    buttonListener()

  }

  private fun setupViewModel() {
    signInViewModel = ViewModelProvider(
      this,
      ViewModelFactory(UserPreference.getInstance(dataStore))
    )[SignInViewModel::class.java]

    signInViewModel.getUser().observe(this) { user ->
      this.user = user
    }
  }

  private fun editTextListener() {
    binding.etEmail.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        setMyButtonEnable()
      }

      override fun afterTextChanged(s: Editable) {
      }
    })
    binding.etPass.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        setMyButtonEnable()
      }

      override fun afterTextChanged(s: Editable) {
      }
    })

    binding.etRegister.setOnClickListener {
      startActivity(Intent(this@SignInActivity, RegisterActivity::class.java))
      finish()
    }
  }

  private fun setMyButtonEnable() {
    binding.myButton.isEnabled =
      binding.etEmail.text.toString().isNotEmpty() &&
          binding.etPass.text.toString().isNotEmpty() &&
          binding.etPass.text.toString().length >= 6 &&
          isEmailValid(binding.etEmail.text.toString())

  }

  private fun isEmailValid(email: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  private fun buttonListener() {
    binding.myButton.setOnClickListener {
      signInViewModel.login()
      AlertDialog.Builder(this).apply {
        setTitle(getString(R.string.information))
        setMessage(getString(R.string.sign_in_success))
        setPositiveButton(getString(R.string.continue_)) { _, _ ->
          val intent = Intent(context, MainActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
          startActivity(intent)
          finish()
        }
        create()
        show()
      }
      
      Toast.makeText(
        this@SignInActivity,
        "${binding.etEmail.text} \n ${binding.etPass.text}",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

}