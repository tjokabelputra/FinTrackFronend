package com.rpl.fintrack.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rpl.fintrack.databinding.FragmentSettingsBinding
import com.rpl.fintrack.ui.factory.TransactionModelFactory
import com.rpl.fintrack.ui.factory.UserPrefModelFactory
import com.rpl.fintrack.ui.login.LoginActivity
import com.rpl.fintrack.ui.userDataStoreViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val prefFactory: UserPrefModelFactory by lazy {
        UserPrefModelFactory.getInstance(requireContext())
    }

    private val userPrefViewModel: userDataStoreViewModel by viewModels {
        prefFactory
    }

    private val factory: TransactionModelFactory by lazy {
        TransactionModelFactory.getInstance(requireContext())
    }

    private val viewModel: SettingsViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
    }

    private fun setupListener() {
        binding.btnLogout.setOnClickListener{
            logOut()
        }
    }

    private fun logOut() {
        AlertDialog.Builder(this@SettingsFragment.requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out from this account")
            .setPositiveButton("Yes") { _, _ ->
                userPrefViewModel.deleteUser()
                viewModel.clearTransactions()
                val intent = Intent(this@SettingsFragment.requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss()}
            .show()
    }
}