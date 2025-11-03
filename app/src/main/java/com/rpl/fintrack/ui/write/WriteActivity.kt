package com.rpl.fintrack.ui.write

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.tabs.TabLayoutMediator
import com.rpl.fintrack.R
import com.rpl.fintrack.databinding.ActivityWriteBinding

class WriteActivity : AppCompatActivity() {

    private var _binding: ActivityWriteBinding? = null
    val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()

        setupEdgetoEdge()
        setupPagerAdapter()
    }

    private fun setupEdgetoEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)

            binding?.statusBarBackground?.updateLayoutParams {
                height = systemBars.top
            }

            insets
        }
    }

    private fun setupPagerAdapter(){
        val pagerAdapter = WritePagerAdapter(this)
        binding?.viewPager?.adapter = pagerAdapter

        TabLayoutMediator(binding!!.tabs, binding!!.viewPager){ tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    companion object{
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_income,
            R.string.tab_expenses
        )
    }
}