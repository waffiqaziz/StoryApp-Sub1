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
import com.dicoding.storyapp.ui.viewmodel.RegisterViewModel
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.databinding.ActivityRegisterBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding
  private lateinit var registerViewModel: RegisterViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivityRegisterBinding.inflate(layoutInflater)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setMyButtonEnable()
    editTextListener()
    buttonListener()
    setupViewModel()
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

    binding.etSignin.setOnClickListener {
      startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
      finish()
    }
  }

  private fun setMyButtonEnable() {
    binding.btnRegister.isEnabled =
      binding.etEmail.text.toString().isNotEmpty() &&
          binding.etPass.text.toString().isNotEmpty() &&
          binding.etPass.text.toString().length >= 6 &&
          isEmailValid(binding.etEmail.text.toString())
  }

  private fun setupViewModel() {
    registerViewModel = ViewModelProvider(
      this,
      ViewModelFactory(UserPreference.getInstance(dataStore))
    )[RegisterViewModel::class.java]
  }

  private fun setupAction() {
    val name = binding.etName.text.toString()
    val email = binding.etEmail.text.toString()
    val password = binding.etPass.text.toString()

    registerViewModel.saveUser(UserModel(name, email, password, false))
    AlertDialog.Builder(this).apply {
      setTitle(getString(R.string.information))
      setMessage("Your account has been created. You can Sign In with your current account.")
      setPositiveButton("Sign In") { _, _ ->
        startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
        finish()
      }
      create()
      show()
    }
  }

  private fun isEmailValid(email: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  private fun buttonListener() {
    binding.btnRegister.setOnClickListener {
      setupAction()
      Toast.makeText(
        this@RegisterActivity,
        "${binding.etEmail.text} \n ${binding.etPass.text}",
        Toast.LENGTH_SHORT
      ).show()
    }
  }
}