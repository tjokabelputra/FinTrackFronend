package com.rpl.fintrack.ui.signup

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
import com.rpl.fintrack.R
import com.rpl.fintrack.databinding.ActivitySignupBinding
import com.rpl.fintrack.ui.factory.AccountModelFactory
import com.rpl.fintrack.ui.login.LoginActivity
import com.rpl.fintrack.database.Result

class SignupActivity : AppCompatActivity() {

    private var _binding: ActivitySignupBinding? = null
    private val binding get() = _binding

    private val factory: AccountModelFactory by lazy {
        AccountModelFactory.getInstance()
    }

    private val viewModel: SignupViewModel by viewModels{
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()

        setupEdgetoEdge()
        setupViewModelObservers()
        setupListener()
    }

    private fun setupEdgetoEdge() {
        binding?.pgRegister?.visibility = View.GONE
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
        binding?.tvClickHere?.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignup?.setOnClickListener{
            val username = binding?.usernameEditText?.text.toString()
            val email = binding?.emailEditText?.text.toString().trim()
            val password = binding?.passwordEditText?.text.toString().trim()
            viewModel.registerAccount(username, email, password)
        }
    }

    private fun setupViewModelObservers() {
        viewModel.registerResult.observe(this) { response ->
            when (response) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleRegisterSuccess(response.data.message)
                is Result.Error -> handleRegisterError(response.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pgRegister?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleRegisterSuccess(message: String?){
        showLoading(false)
        val intent = Intent(this, LoginActivity::class.java)
        AlertDialog.Builder(this)
            .setTitle("Registration Successful")
            .setMessage(message)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
                startActivity(intent)
            }
            .show()
    }

    private fun handleRegisterError(errorMessage: String){
        showLoading(false)
        AlertDialog.Builder(this)
            .setTitle("Registration Failed")
            .setMessage(errorMessage)
            .setNegativeButton("Continue") { dialog, _ -> dialog.dismiss() }
            .show()
        Log.e("SignUpActivity", "Error: $errorMessage")
    }
}