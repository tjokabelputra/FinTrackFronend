package com.rpl.fintrack.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.rpl.fintrack.R
import com.rpl.fintrack.databinding.ActivityLoginBinding
import com.rpl.fintrack.ui.factory.AccountModelFactory
import com.rpl.fintrack.ui.signup.SignupActivity
import com.rpl.fintrack.database.Result
import com.rpl.fintrack.database.remote.response.LoginResponse
import com.rpl.fintrack.database.userDataStore
import com.rpl.fintrack.database.userPreference
import com.rpl.fintrack.ui.factory.UserPrefModelFactory
import com.rpl.fintrack.ui.list.TransactionsActivity
import com.rpl.fintrack.ui.userDataStoreViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private val factory: AccountModelFactory by lazy {
        AccountModelFactory.getInstance()
    }

    private val viewModel: LoginViewModel by viewModels {
        factory
    }

    private val prefFactory: UserPrefModelFactory by lazy {
        UserPrefModelFactory.getInstance(this)
    }

    private val userPrefViewModel: userDataStoreViewModel by viewModels {
        prefFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()

        checkLoggedAccount()
        setupEdgetoEdge()
        setupViewModelObservers()
        setupListener()
    }

    private fun checkLoggedAccount() {
        val pref = userPreference.getInstance(userDataStore)

        lifecycleScope.launch {
            val user = pref.getUser().first()
            if (user.uid.isNotEmpty() && user.token.isNotEmpty()){
                startActivity(Intent(this@LoginActivity, TransactionsActivity::class.java))
            }
        }
    }

    private fun setupEdgetoEdge() {
        binding?.pgLogin?.visibility = View.GONE

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

            binding?.statusBarBackground?.updateLayoutParams {
                height = systemBars.bottom
            }
            insets
        }
    }

    private fun setupListener() {
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString().trim()
            val password = binding?.passwordEditText?.text.toString().trim()
            viewModel.loginAccount(email, password)
        }

        binding?.tvClickHere?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setupViewModelObservers() {
        viewModel.loginResult.observe(this, Observer { response ->
            when (response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleLoginSuccess(response.data)
                is Result.Error -> handleLoginError(response.error)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pgLogin?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleLoginSuccess(userData: LoginResponse) {
        showLoading(false)
        userPrefViewModel.saveUser(userData.account?.uid.toString(), userData.token.toString())
        val intent = Intent(this, TransactionsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun handleLoginError(errorMessage: String) {
        showLoading(false)
        AlertDialog.Builder(this)
            .setTitle("Login Failed")
            .setMessage(errorMessage)
            .setNegativeButton("Continue") { dialog, _ -> dialog.dismiss() }
            .show()
        Log.e("LoginActivity", "Error: $errorMessage")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}