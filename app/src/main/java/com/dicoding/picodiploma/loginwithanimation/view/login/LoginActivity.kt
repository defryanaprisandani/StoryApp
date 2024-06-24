package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.Result
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference.getInstance(dataStore)

        binding.progressBar.visibility = View.GONE

        setupView()
        setupAction()
        checkSession()
        observeViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()) {
                binding.emailEditTextLayout.error = getString(R.string.email_invalid)
            } else if (password.length < 8) {
                binding.passwordEditTextLayout.error = getString(R.string.password_error_message)
            } else {
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
                viewModel.loginUser(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val loginResult = result.data.loginResult
                            if (loginResult != null) {
                                lifecycleScope.launch {
                                    userPreference.saveSession(
                                        UserModel(
                                            email = email,
                                            token = loginResult.token,
                                            isLogin = true
                                        )
                                    )
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                showErrorDialog(getString(R.string.error_login))
                            }
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showErrorDialog(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.loginResult.observe(this) { result ->
            if (result) {
                showSuccessDialog()
            } else {
                showErrorDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeay!")
            setMessage(getString(R.string.login_succeed))
            setPositiveButton(getString(R.string.continue_text)) { _, _ ->
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(errorMessage: String = getString(R.string.error_login)) {
        AlertDialog.Builder(this).apply {
            setTitle("Yah :(")
            setMessage("$errorMessage. Silahkan coba lagi")
            setPositiveButton("Lanjut") { _, _ ->
                binding.emailEditText.setText("")
                binding.passwordEditText.setText("")
            }
            create()
            show()
        }
    }

    private fun checkSession() {
        lifecycleScope.launch {
            val userSession = userPreference.getSession().first()
            if (userSession.isLogin) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
